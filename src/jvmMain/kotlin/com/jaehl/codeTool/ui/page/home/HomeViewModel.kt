package com.jaehl.codeTool.ui.page.home

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
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlinx.coroutines.flow.collect

class HomeViewModel(
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateParser: TemplateParser,
    private val templateCreator : TemplateCreator,
    private val templateRepo : TemplateRepo
) : ViewModel() {

    private lateinit var project : Project

    private var template : Template? = null

    var templateFileOutputs = mutableStateListOf<TemplateFileOutput>()
        private set

    var selectedTemplateIndex = mutableStateOf<Int>(-1)
        private set

    var variables = mutableStateListOf<Variable>()

    private var selectedVariablePackageName = ""

    var isPackagePickerDialogOpen = mutableStateOf<Boolean>(false)
        private set

    var packages = mutableStateListOf<PackageData>()
        private set

    var templates = mutableStateListOf<Template>()
        private set

    private fun getFolders(srcPath : Path) : List<Path>{
        var result: List<Path?>
        Files.walk(srcPath).use { walk ->
            result = walk.filter(Files::isDirectory)
                .collect(Collectors.toList())
        }
        return result.filterNotNull()
    }

    fun init(viewModelScope: CoroutineScope, project : Project) {
        super.init(viewModelScope)
        this.project = project
        viewModelScope.launch {
            templateRepo.getTemplates().collect { templatesList ->
                templates.postSwap(templatesList)
            }
        }
    }

    private fun areVariablesSet() : Boolean{
        variables.toList().forEach {
            when(it) {
                is VariableString -> {
                    if (it.value.isEmpty()) return false
                }
                is VariablePackage -> {
                    if (it.packageImport.isEmpty()) return false
                }
            }
        }
        return true
    }

    private fun parseTemplate() : List<TemplateFileOutput>{
        var values = hashMapOf<String, String>(
            "USER_DIR" to System.getProperty("user.home")
        )
        values["TEMPLATE_DIR"] = templateParser.parseString(template!!.dirPath, values)
        values["projectPackage"] = project.mainPackage.replace("\\", ".").substring(1);

        variables.toList().forEach {
            when(it) {
                is VariableString -> {
                    values[it.name] = if(!it.value.isNullOrBlank()) it.value else "{{${it.name}}}"
                }
                is VariablePackage -> {
                    values["${it.name}Import"] = if(!it.packageImport.isNullOrBlank()) it.packageImport else "{{${it.name}}}"
                    values["${it.name}Path"] = if(!it.path.toString().isNullOrBlank()) it.path.toString()  else "{{${it.name}}}"
                }
            }
        }
        return templateParser.parse(template!!, values)
    }

    private fun onGenerateTemplate() = viewModelScope.launch {
        val output : List<TemplateFileOutput> = parseTemplate()
        templateFileOutputs.postSwap(output)
    }

    fun onSaveTemplateClick() = viewModelScope.launch {
        if( !areVariablesSet()) return@launch

        val output : List<TemplateFileOutput> = parseTemplate()
        templateFileOutputs.postSwap(output)

        templateCreator.createFile(output)
    }

    fun onTemplateSelectClick(newTemplate : Template, index : Int) = viewModelScope.launch {
        selectedTemplateIndex.value = index
        template = newTemplate
        val tempVariable = newTemplate.variable.map {
            return@map when (it.type){
                TemplateVariableType.Package -> {
                    VariablePackage(it.name, "", Path.of(""), "")
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

    fun onVariableStringChange(name : String, value : String) = viewModelScope.launch {
        val temp = variables.toList()
        (temp.firstOrNull{it.name == name} as? VariableString)?.value = value
        variables.postSwap(temp)
        onGenerateTemplate()
    }

    fun onOpenPackagePickerDialog(variableName : String) = viewModelScope.launch {
        val packagePath = Path.of(project.projectPath+project.kotlinSrcPath+project.mainPackage)
        val srcPathPath = Path.of(project.projectPath+project.kotlinSrcPath)
        val packageList = getFolders(packagePath)
            .mapNotNull {
                return@mapNotNull if(packagePath.count() == it.count()) null
                else PackageData(
                    path = it,
                    packageImport = it.subpath(srcPathPath.count(), it.count()).toString().replace("\\", "."),
                    name = it.subpath(packagePath.count(), it.count()).toString()
                )
            }
        selectedVariablePackageName = variableName
        packages.postSwap(packageList)
        isPackagePickerDialogOpen.value = true
    }

    fun onClosePackagePickerDialog() = viewModelScope.launch {
        isPackagePickerDialogOpen.value = false
        selectedVariablePackageName = ""
    }

    fun onSelectedPackageClick(packageData : PackageData) = viewModelScope.launch {
        isPackagePickerDialogOpen.value = false

        val temp = variables.toList()
        val tempVariablePackage = temp.firstOrNull{it.name == selectedVariablePackageName} as? VariablePackage
        tempVariablePackage?.stringValue = packageData.name
        tempVariablePackage?.path = packageData.path
        tempVariablePackage?.packageImport = packageData.packageImport
        variables.postSwap(temp)

        selectedVariablePackageName = ""
        onGenerateTemplate()
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
    ) : Variable(name)

    class VariablePackage(
        name : String,
        var stringValue : String,
        var path : Path,
        var packageImport : String
    ) : Variable(name)
}