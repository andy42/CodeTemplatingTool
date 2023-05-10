package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.data.model.*
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.TextFieldData
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class TemplateEditViewModel @Inject constructor(
    private val logger : Logger,
    private val templateRepo : TemplateRepo,
    private val templateEditValidator : TemplateEditValidator
) : ViewModel(), TemplateEditValidatorListener {

    private var template : Template? = null

    var navBackListener : NavBackListener? = null
    var navTemplateEditDialogListener : NavTemplateEditDialogListener? = null

    var name = mutableStateOf<TextFieldData>(TextFieldData())

    var variables = mutableStateListOf<TemplateVariable>()
    var files = mutableStateListOf<TemplateFileViewModel>()
        private set

    var selectedNavRow = mutableStateOf<NavRowSelect>(NavRowSelect.NavRowGeneralInfoSelect)

    var isSaveEnabled = mutableStateOf<Boolean>(false)

    private var templateFileNextID = 0

    var templateFilePath = mutableStateOf<TextFieldData>(TextFieldData())
    var templateFilePathDestination = mutableStateOf<TextFieldData>(TextFieldData())
    var templateFileData = mutableStateOf<String>("")

    fun init(viewModelScope: CoroutineScope, template : Template?) {
        super.init(viewModelScope)

        this.template = template

        templateEditValidator.setValidatorListener(this)

        viewModelScope.launch {
            val template = this@TemplateEditViewModel.template

            name.value = TextFieldData(value = template?.name ?: "")
            if(template != null) {
                loadTemplateFiles(template = template)
            }
            validateTemplate()

            variables.postSwap(template?.variable ?: listOf())
        }
    }

    private suspend fun loadTemplateFiles(template : Template){
        files.postSwap(
            templateRepo.getTemplate(template.id)?.files?.mapIndexed{ index, templateFile ->
                templateFileNextID = index + 1
                return@mapIndexed TemplateFileViewModel.create(
                    index,
                    templateFile,
                    templateRepo.loadTemplateFile(template, templateFile)
                )

            } ?: listOf())
    }

    private fun validateTemplate(){
        isSaveEnabled.value = templateEditValidator.validateTemplateName(currentTemplate = template, name = name.value.value)
    }

    private fun validateTemplateFile(){
        val fileIndex = (selectedNavRow.value as? NavRowSelect.NavRowFileSelect)?.index ?: return
        val file = files[fileIndex]
        isSaveEnabled.value = templateEditValidator.validateTemplateFile(
            filePaths = files.toList().map { it.path },
            currentPath = file.path,
            path = templateFilePath.value.value,
            pathDestination = templateFilePathDestination.value.value)
    }

    fun onNameChange(name : String){
        this@TemplateEditViewModel.name.value = TextFieldData(value = name)
        validateTemplate()
    }

    fun onGeneralInfoClick() {
        val newNavRowGeneralInfoSelect = NavRowSelect.NavRowGeneralInfoSelect
        if(checkIfCurrentPageSaved()) {
            onChangeNavPage(newNavRowGeneralInfoSelect)
        } else {
            navTemplateEditDialogListener?.showWarningDialog(
                WarningDialogConfig(
                    message = "Do you want to change page without saving? your current changes will be lost",
                    acceptText = "yes",
                    declineText = "No",
                    acceptCallBack = {
                        onChangeNavPage(newNavRowGeneralInfoSelect)
                    }
                )
            )
        }
    }

    fun onTemplateFileClick(index : Int ) = viewModelScope.launch {
        val newNavRowFileSelect = NavRowSelect.NavRowFileSelect(index)

        if(checkIfCurrentPageSaved()) {
            onChangeNavPage(newNavRowFileSelect)
        } else {
            navTemplateEditDialogListener?.showWarningDialog(
                WarningDialogConfig(
                    message = "Do you want to change page without saving? your current changes will be lost",
                    acceptText = "yes",
                    declineText = "No",
                    acceptCallBack = {
                        onChangeNavPage(newNavRowFileSelect)
                    }
                )
            )
        }
    }

    fun onTemplateVariableNameChange(variableIndex : Int, name : String) {
        variables[variableIndex] = variables[variableIndex].copy(name = name)
    }

    fun onTemplateVariableTypeClick(variableIndex : Int ) {
        navTemplateEditDialogListener?.showTypeVariablePickerDialog(variableIndex)
    }

    fun onTemplateVariableTypeChange(index : Int, type : TemplateVariableType) = viewModelScope.launch {
        variables[index] = variables[index].copy(type = type)
    }

    fun onTemplateVariableDefaultChange(variableIndex : Int, default : String) {
        variables[variableIndex] = variables[variableIndex].copy(default = default)
    }

    fun onTemplateVariableStartPathChange(variableIndex : Int, startPath : String) {
        variables[variableIndex] = variables[variableIndex].copy(startPath = startPath)
    }

    fun onTemplateVariableDelete(variableIndex : Int ) {
        variables.removeAt(variableIndex)
    }

    fun onTemplateFilePathChange(path : String) {
        templateFilePath.value = TextFieldData(value = path)
        validateTemplateFile()
    }

    fun onTemplateFilePathDestinationChange(pathDestination : String) {
        templateFilePathDestination.value = TextFieldData(value = pathDestination)
        validateTemplateFile()
    }

    fun addVariable() = viewModelScope.launch {
        variables.add(TemplateVariable(
            name = "",
            type = TemplateVariableType.String,
            default = ""
        ))
    }

    fun addTemplateFile() = viewModelScope.launch {
        val template = this@TemplateEditViewModel.template
        if(template == null){
            navTemplateEditDialogListener?.showWarningDialog(
                WarningDialogConfig(
                    message = "To add a file, you most first save"
                )
            )
            return@launch
        }

        templateRepo.addNewTemplateFile(template)
        loadTemplateFiles(template)
    }

    fun deleteTemplateFile(id : Int) = viewModelScope.launch {
        template?.let { template ->
            selectedNavRow.value = NavRowSelect.NavRowGeneralInfoSelect
            if(templateRepo.deleteTemplateFile(template.id, files[id].toTemplateFile())){
                loadTemplateFiles(template)
            }
        }
    }

    private fun checkIfTemplateFileSaved(navRowFileSelect : NavRowSelect.NavRowFileSelect) : Boolean {
        val template = this@TemplateEditViewModel.template ?: return true

        val templateFile = files[navRowFileSelect.index].toTemplateFile()
        if(templateFile?.path != templateFilePath.value.value){
            return false
        }
        if(templateFile?.pathDestination != templateFilePathDestination.value.value){
            return false
        }

        if(templateRepo.loadTemplateFile(template, templateFile) != templateFileData.value){
            return false
        }

        return true
    }

    private fun checkIfGeneralInfoSaved() : Boolean {
        val template = this@TemplateEditViewModel.template ?: return false

        if(template.name != name.value.value) {
            return false
        }

        if (template.variable.size != variables.size){
            return false
        }

        template.variable.forEachIndexed { index, templateVariable ->
            if (templateVariable != variables[index]) {
                return false
            }
        }

        return true
    }

    private fun checkIfCurrentPageSaved() : Boolean {
        return when (val selected = selectedNavRow.value) {
            is NavRowSelect.NavRowFileSelect -> {
                checkIfTemplateFileSaved(selected)
            }
            is NavRowSelect.NavRowGeneralInfoSelect -> {
                checkIfGeneralInfoSaved()
            }
        }
    }

    fun saveTemplateFile(id : Int) = viewModelScope.launch {

        val newFiles = files.toMutableList()
        val index = newFiles.indexOfFirst { it.id == id}

        templateRepo.updateTemplateFile(
            templateId = template?.id ?: "",
            currentPath = newFiles[index].savedPath ?: "",
            newTemplateFile = TemplateFile(
                path = templateFilePath.value.value,
                pathDestination = templateFilePathDestination.value.value
            ),
            fileData = templateFileData.value
        )
        val updatedTemplate = templateRepo.getTemplate(template?.id) ?: return@launch
        template = updatedTemplate
        loadTemplateFiles(updatedTemplate)
    }

    fun deleteTemplate() = viewModelScope.launch {
        template?.let {
            templateRepo.deleteTemplate(it)
            navBackListener?.navigateBack()
        }
    }
    fun save() = viewModelScope.launch {
        template = templateRepo.updateTemplate(
            oldTemplate = this@TemplateEditViewModel.template,
            newTemplate = Template(
                id = template?.id ?: "",
                name = name.value.value,
                variable = variables.toList(),
                files = files.toList().map { it.toTemplateFile() }
            )
        )
    }

    fun onCloseClick() {
        if(checkIfCurrentPageSaved()){
            navBackListener?.navigateBack()
        } else {
            navTemplateEditDialogListener?.showWarningDialog(
                WarningDialogConfig(
                    message = "Do you want to close without saving? your current changes will be lost",
                    acceptText = "yes",
                    declineText = "No",
                    acceptCallBack = {
                        closeWithoutSaving()
                    }
                )
            )

        }
    }

    fun closeWithoutSaving() {
        navBackListener?.navigateBack()
    }

    override fun onTemplateNameError(error: String) {
        name.value = name.value.copy(error = error)
    }

    override fun onTemplateFilePathError(error : String) {
        templateFilePath.value = templateFilePath.value.copy(error = error)
    }
    override fun onTemplateFilePathDestinationError(error : String){
        templateFilePathDestination.value = templateFilePathDestination.value.copy(error = error)
    }

    private fun onChangeNavPage(navRowSelect: NavRowSelect) = viewModelScope.launch {
        if(navRowSelect is NavRowSelect.NavRowFileSelect) {
            selectedNavRow.value = navRowSelect
            val templateFile = files[navRowSelect.index]

            templateFilePath.value = TextFieldData(value = templateFile.path)
            templateFilePathDestination.value = TextFieldData(value = templateFile.pathDestination)
            templateFileData.value = templateFile.fileData
            validateTemplateFile()
        }
        else if(navRowSelect is NavRowSelect.NavRowGeneralInfoSelect) {
            name.value = name.value.copy(value = template?.name ?: "")
            variables.postSwap(template?.variable ?: listOf())
            selectedNavRow.value = navRowSelect
            validateTemplate()
        }
    }

    sealed class NavRowSelect {
        object NavRowGeneralInfoSelect : NavRowSelect()
        class NavRowFileSelect(val index : Int) : NavRowSelect()
    }
}

data class TemplateFileViewModel(
    val id : Int,
    var name : String = "",
    var path : String = "",
    val savedPath : String? = null,
    var pathDestination : String = "",
    var fileData : String = ""
) {
    companion object {
        fun create(index : Int, templateFile : TemplateFile, fileData : String) : TemplateFileViewModel {
            return TemplateFileViewModel(
                id = index,
                name = templateFile.path.split(File.separator).lastOrNull() ?: "",
                path = templateFile.path,
                savedPath = templateFile.path,
                pathDestination = templateFile.pathDestination,
                fileData = fileData
            )
        }
    }

    fun toTemplateFile() : TemplateFile{
        return TemplateFile(
            path = path,
            pathDestination = pathDestination
        )
    }
}