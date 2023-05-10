package com.jaehl.codeTool.ui.page.templateApply

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateFileOutput
import com.jaehl.codeTool.data.model.TemplateVariableType
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.data.templateCreator.TemplateCreator
import com.jaehl.codeTool.data.templateParser.TemplateParser
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.navigation.NavTemplateListener
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject

class TemplateApplyViewModel @Inject constructor(
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateParser: TemplateParser,
    private val templateCreator : TemplateCreator,
    private val templateRepo : TemplateRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navTemplateListener : NavTemplateListener? = null
    var navTemplateApplyDialogListener : NavTemplateApplyDialogListener? = null

    private lateinit var project : Project

    private var template : Template? = null

    var templateFileOutputs = mutableStateListOf<TemplateFileOutput>()
        private set

    var selectedTemplateIndex = mutableStateOf<Int>(-1)
        private set

    var variables = mutableStateListOf<Variable>()

    var templates = mutableStateListOf<Template>()
        private set

    fun init(viewModelScope: CoroutineScope, project : Project) {
        super.init(viewModelScope)
        this.project = project
        viewModelScope.launch {
            templateRepo.getTemplates().collect { templatesList ->
                templates.postSwap(templatesList)
                if(templatesList.isNotEmpty()) {
                    onTemplateSelectClick(templatesList.first(), 0)
                }
            }
        }
    }

    private fun areVariablesSet() : Boolean{
        variables.toList().forEach {
            when(it) {
                is VariableString -> {
                    if (it.value.isEmpty()) return false
                }
                is VariablePath -> {
                    if (it.value.isEmpty()) return false
                }
            }
        }
        return true
    }

    private fun convertToImport(path : String) : String {
        return path.replace(fileUtil.getPathSeparator(), ".").substring(1)
    }

    private fun createProjectVariables() : HashMap<String, String>{
        var values = hashMapOf<String, String>()

        project.variable.forEach {variable ->
            when(variable.type) {
                else -> {
                    values[variable.name] = variable.value
                    values["${variable.name}\$import"] = convertToImport(variable.value)
                }
            }
        }
        return values
    }

    private fun parseTemplate() : List<TemplateFileOutput>{

        var values = createProjectVariables()

        variables.toList().forEach { variable ->
            when(variable) {
                is VariableString -> {
                    values["${variable.name}"] = if(!variable.value.isNullOrBlank()) variable.value else "{{${variable.name}}}"
                    values["${variable.name}\$import"] = if(!variable.value.isNullOrBlank()) convertToImport(variable.value) else "{{${variable.name}}}"
                }
                is VariablePath -> {
                    values["${variable.name}"] = if(!variable.value.isNullOrBlank()) variable.value else "{{${variable.name}}}"
                    values["${variable.name}\$import"] = if(!variable.value.isNullOrBlank()) convertToImport(variable.value) else "{{${variable.name}}}"
                }
            }
        }
        return templateParser.parse(project, template!!, values)
    }

    private fun onGenerateTemplate() = viewModelScope.launch {
        val output : List<TemplateFileOutput> = parseTemplate()
        templateFileOutputs.postSwap(output)
    }

    private fun checkNewFilesDoNotOverridePrevious(output : List<TemplateFileOutput>) : Boolean {
        output.forEach {
            if(fileUtil.fileExists(Paths.get(it.path))){
                return true
            }
        }
        return false
    }
    fun onApplyTemplateClick() = viewModelScope.launch {
        if( !areVariablesSet()) return@launch

        val output : List<TemplateFileOutput> = parseTemplate()
        templateFileOutputs.postSwap(output)

        if(checkNewFilesDoNotOverridePrevious(output)){
            navTemplateApplyDialogListener?.showWarningDialog(
                WarningDialogConfig(
                    message = "The current template settings will override current files, do you wish to continue",
                    acceptText = "yes",
                    declineText = "No",
                    acceptCallBack = {
                        templateCreator.createFile(output)
                    }
                )
            )
        } else {
            templateCreator.createFile(output)
        }
    }

    fun onTemplateSelectClick(newTemplate : Template, index : Int) = viewModelScope.launch {

        var values = createProjectVariables()

        selectedTemplateIndex.value = index
        template = newTemplate
        val tempVariable = newTemplate.variable.map {
            return@map when (it.type){
                TemplateVariableType.Path -> {
                    VariablePath(
                        name = it.name,
                        value = "",
                        startPath = templateParser.parseString(
                            project.projectPath+it.startPath,
                            values))
                }
                else ->{
                    VariableString(it.name, it.default)
                }
            }
        }
        templateFileOutputs.postSwap(listOf())
        variables.postSwap(tempVariable)
        onGenerateTemplate()
    }

    fun onVariableStringChange(index : Int, value : String) = viewModelScope.launch {
        val variableString = (variables[index] as? VariableString) ?: return@launch
        variables[index] = variableString.copy(value = value)

        viewModelScope.launch {
            onGenerateTemplate()
        }
    }

    fun onOpenPathPickerDialog(index : Int, variableName : String) = viewModelScope.launch {
        val temp = variables.toList()
        val startPath = (temp[index] as? VariablePath)?.startPath ?: return@launch
        if (!fileUtil.fileExists(Paths.get(startPath))){
            navTemplateApplyDialogListener?.showWarningDialog(
                WarningDialogConfig(
                    title = "Error",
                    message = "picker start Path does not exist : $startPath"
                )
            )
            return@launch
        }
        navTemplateApplyDialogListener?.showFolderPickerDialog(variableName, startPath)
    }

    fun onProjectPathChange(requestId : String, path : String) = viewModelScope.launch {
        val temp = variables.toList()
        val tempVariablePath = temp.firstOrNull{it.name == requestId} as? VariablePath
        tempVariablePath?.value = path
        variables.postSwap(temp)
        onGenerateTemplate()
    }

    fun onOpenTemplateList() {
        navTemplateListener?.openTemplateList()
    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    data class PackageData(
        val path : Path,
        val packageImport : String,
        val name : String,
    )

    abstract class Variable(
        val name : String
    )

    class VariableString(
        name : String,
        var value : String = ""
    ) : Variable(name) {
        fun copy(name : String? = null, value : String? = null) : VariableString {
            return VariableString(
                name = name ?: this.name,
                value = value ?: this.value,
            )
        }
    }

    class VariablePath (
        name : String,
        var value : String,
        var startPath : String
    ) : Variable(name)
}