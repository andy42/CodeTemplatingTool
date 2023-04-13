package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.data.model.*
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.TextFieldData
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class TemplateEditViewModel(
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
    private val templateEditValidator : TemplateEditValidator,
    private var template : Template?,
    private val showTypeVariablePickerDialog : (index : Int) -> Unit,
    private val onClose: () -> Unit
) : ViewModel(), TemplateEditValidatorListener {

    var name = mutableStateOf<TextFieldData>(TextFieldData(value = template?.name ?: ""))

    var dirPath = mutableStateOf<TextFieldData>(TextFieldData(value = template?.dirPath ?: ""))
    var variables = mutableStateListOf<TemplateVariable>()
    var files = mutableStateListOf<TemplateFileViewModel>()
        private set

    private val filesToDelete = arrayListOf<TemplateFileViewModel>()

    var selectedNavRow = mutableStateOf<NavRowSelect>(NavRowSelect.NavRowGeneralInfoSelect)

    var isSaveEnabled = mutableStateOf<Boolean>(false)

    private var templateFileNextID = 0

    var templateFilePath = mutableStateOf<TextFieldData>(TextFieldData())
    var templateFilePathDestination = mutableStateOf<TextFieldData>(TextFieldData())
    var templateFileData = mutableStateOf<String>("")

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        templateEditValidator.setValidatorListener(this)

        viewModelScope.launch {
            val template = this@TemplateEditViewModel.template
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
        isSaveEnabled.value = templateEditValidator.validateTemplate(currentTemplate = template, name = name.value.value, dirPath = dirPath.value.value)
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

    fun onDirPathChange(dirPath : String) {
        this@TemplateEditViewModel.dirPath.value = TextFieldData(value = dirPath)
        validateTemplate()
    }

    fun onGeneralInfoClick() {
        selectedNavRow.value = NavRowSelect.NavRowGeneralInfoSelect
    }

    fun onTemplateFileClick(index : Int ) = viewModelScope.launch {
        selectedNavRow.value = NavRowSelect.NavRowFileSelect(index)
        val templateFile = files[index]

        templateFilePath.value = TextFieldData(value = templateFile.path)
        templateFilePathDestination.value = TextFieldData(value = templateFile.pathDestination)
        templateFileData.value = templateFile.fileData
        validateTemplateFile()
    }

    fun onTemplateVariableNameChange(variableIndex : Int, name : String) {
        variables[variableIndex] = variables[variableIndex].copy(name = name)
    }

    fun onTemplateVariableTypeClick(variableIndex : Int ) {
        showTypeVariablePickerDialog(variableIndex)
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

    fun openDefaultVariablePickerDialog() {

    }

    fun addVariable() = viewModelScope.launch {
        variables.add(TemplateVariable(
            name = "",
            type = TemplateVariableType.String,
            default = ""
        ))
    }

    fun addTemplateFile() = viewModelScope.launch {
        val template = this@TemplateEditViewModel.template ?: return@launch
        templateRepo.addNewTemplateFile(template)
        loadTemplateFiles(template)
    }

    fun deleteTemplateFile(id : Int) = viewModelScope.launch {
        template?.let { template ->
            if(templateRepo.deleteTemplateFile(template.id, files[id].toTemplateFile())){
                loadTemplateFiles(template)
                selectedNavRow.value = NavRowSelect.NavRowGeneralInfoSelect
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
            onClose()
        }
    }
    fun save() = viewModelScope.launch {
        template = templateRepo.updateTemplate(
            Template(
                id = template?.id ?: "",
                name = name.value.value,
                dirPath = dirPath.value.value,
                variable = variables.toList(),
                files = files.toList().map { it.toTemplateFile() }
            )
        )
    }

    fun onCloseClick() {
        onClose()
    }

    override fun onTemplateNameError(error: String) {
        name.value = name.value.copy(error = error)
    }

    override fun onTemplateDirError(error: String) {
        dirPath.value = dirPath.value.copy(error = error)
    }

    override fun onTemplateFilePathError(error : String) {
        templateFilePath.value = templateFilePath.value.copy(error = error)
    }
    override fun onTemplateFilePathDestinationError(error : String){
        templateFilePathDestination.value = templateFilePathDestination.value.copy(error = error)
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