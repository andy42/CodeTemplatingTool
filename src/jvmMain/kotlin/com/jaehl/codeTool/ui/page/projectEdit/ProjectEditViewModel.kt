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
    private val project : Project?,
    private val showFolderPickerDialog : (currentPath : String?) -> Unit,
    private val showListPickerDialog : (index : Int) -> Unit,
    private val showDefaultVariablePickerDialog : () -> Unit,
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
            if(project != null){
                projectName.value = project.name
                projectPath.value = project.projectPath
                projectVariables.postSwap(project.variable)
            }
            isSaveEnabled.value = validateProject()
        }
    }

    fun onProjectNameChange(value : String) = viewModelScope.launch {
        projectName.value = value

        isSaveEnabled.value = validateProject()
    }

    fun onProjectVariableNameChange(index : Int, name : String) = viewModelScope.launch {
        var projectVariables = this@ProjectEditViewModel.projectVariables.toMutableList()
        projectVariables[index]?.name = name
        this@ProjectEditViewModel.projectVariables.postSwap(projectVariables)
    }

    fun onProjectVariableTypeClick(index : Int) = viewModelScope.launch {
        showListPickerDialog(index)
    }

    fun onProjectVariableTypeChange(index : Int, type : TemplateVariableType) = viewModelScope.launch {
        var projectVariables = this@ProjectEditViewModel.projectVariables.toMutableList()
        projectVariables[index]?.type = type
        this@ProjectEditViewModel.projectVariables.postSwap(projectVariables)
    }

    fun onProjectVariableValueChange(index : Int, value : String) = viewModelScope.launch {
        var projectVariables = this@ProjectEditViewModel.projectVariables.toMutableList()
        projectVariables[index]?.value = value
        this@ProjectEditViewModel.projectVariables.postSwap(projectVariables)
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

    fun onProjectPathChange(requestId : String, path : String) = viewModelScope.launch {
        projectPath.value = path
        isSaveEnabled.value = validateProject()
    }

    private fun validateProject() : Boolean{
        if(projectName.value.isEmpty()) return false
        if(projectPath.value.isEmpty()) return false
        return true
    }

    fun save() = viewModelScope.launch {
        var newProject = Project(
            name = projectName.value,
            projectPath = projectPath.value,
            variable = projectVariables.toList()
        )

        projectRepo.updateProject(newProject)
    }
}