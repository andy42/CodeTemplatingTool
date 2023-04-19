package com.jaehl.codeTool.ui.page.projectEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.TemplateVariableType
import com.jaehl.codeTool.data.model.ProjectVariable
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProjectEditViewModel(
    private val logger : Logger,
    private val projectRepo : ProjectRepo,
    private var project : Project?,
    private val showFolderPickerDialog : (currentPath : String?) -> Unit,
    private val showListPickerDialog : (index : Int) -> Unit,
    private val showDefaultVariablePickerDialog : () -> Unit,
    private val onGoBackClicked: () -> Unit,
    private val showCloseWithoutSavingDialog: () -> Unit
) : ViewModel() {

    var projectName = mutableStateOf<String>("")
        private set

    var projectPath = mutableStateOf<String>("")
        private set

    var isSaveEnabled = mutableStateOf<Boolean>(false)

    var projectVariables = mutableStateListOf<ProjectVariable>()

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)


        viewModelScope.launch {
            project?.let {
                projectName.value = it.name
                projectPath.value = it.projectPath
                projectVariables.postSwap(it.variable)
            }
            isSaveEnabled.value = validateProject()
        }
    }

    fun onProjectNameChange(value : String) = viewModelScope.launch {
        projectName.value = value
        isSaveEnabled.value = validateProject()
    }

    fun onProjectVariableNameChange(index : Int, name : String) {
        projectVariables[index] = projectVariables[index].copy(name = name)
    }

    fun onProjectVariableTypeClick(index : Int) = viewModelScope.launch {
        showListPickerDialog(index)
    }

    fun onProjectVariableTypeChange(index : Int, type : TemplateVariableType) {
        projectVariables[index] = projectVariables[index].copy(type = type)
    }

    fun onProjectVariableValueChange(index : Int, value : String) {
        projectVariables[index] = projectVariables[index].copy(value = value)
    }

    fun onProjectVariableDelete(index : Int) = viewModelScope.launch {
        projectVariables.removeAt(index)
    }

    fun addVariable() = viewModelScope.launch {
        projectVariables.add(ProjectVariable(
            name = "",
            type = TemplateVariableType.String,
            value = ""
        ))
    }

    fun onOpenPackagePickerDialog() = viewModelScope.launch {
        showFolderPickerDialog(
            if(projectPath.value.isNotEmpty()) projectPath.value else null
        )
    }

    fun openDefaultVariablePickerDialog()  = viewModelScope.launch {
        showDefaultVariablePickerDialog()
    }

    fun onAddDefaultVariable(projectVariable : ProjectVariable) = viewModelScope.launch {
        projectVariables.add(projectVariable)
    }

    fun onProjectPathChange(requestId : String, path : String) {
        projectPath.value = path
        isSaveEnabled.value = validateProject()
    }

    private fun validateProject() : Boolean{
        if(projectName.value.isEmpty()) return false
        if(projectPath.value.isEmpty()) return false
        return true
    }

    private fun checkIfUnsaved() : Boolean {
        if ( project?.name != projectName.value){
            return true
        }
        if ( project?.projectPath != projectPath.value){
            return true
        }

        if ( project?.variable?.size != projectVariables.size){
            return true
        }
        project?.variable?.forEachIndexed {index, projectVariable ->
            if(projectVariables[index] != projectVariable){
                return true
            }
        }

        return false
    }
    fun onNavBackClick() = viewModelScope.launch {
        if(checkIfUnsaved()){
            showCloseWithoutSavingDialog()
        } else {
            onGoBackClicked()
        }
    }

    fun save() = viewModelScope.launch {
        var newProject = Project(
            name = projectName.value,
            projectPath = projectPath.value,
            variable = projectVariables.toList()
        )

        project = projectRepo.updateProject(newProject)
    }

    fun delete() = viewModelScope.launch {
        project?.let {
            projectRepo.deleteProject(it.id)
            onGoBackClicked()
        }
    }

    fun closeWithoutSaving() {
        onGoBackClicked()
    }
}