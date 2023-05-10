package com.jaehl.codeTool.ui.page.projectEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.TemplateVariableType
import com.jaehl.codeTool.data.model.ProjectVariable
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProjectEditViewModel @Inject constructor(
    private val logger : Logger,
    private val fileUtil: FileUtil,
    private val projectRepo : ProjectRepo,
    private val osPathConverter : OsPathConverter
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navProjectEditDialogListener : NavProjectEditDialogListener? = null

    private var project : Project? = null
    var projectName = mutableStateOf<String>("")
        private set

    var projectPath = mutableStateOf<String>("")
        private set

    var isSaveEnabled = mutableStateOf<Boolean>(false)

    var projectVariables = mutableStateListOf<ProjectVariable>()

    fun init(viewModelScope: CoroutineScope, project : Project?) {
        super.init(viewModelScope)

        this.project = project

        viewModelScope.launch {
            project?.let {
                projectName.value = it.name
                projectPath.value = it.projectPath
                projectVariables.postSwap(it.variable)
            }
            isSaveEnabled.value = validateProject()
        }
    }

    fun onProjectNameChange(value : String) {
        projectName.value = value
        isSaveEnabled.value = validateProject()
    }

    fun onProjectVariableNameChange(index : Int, name : String) {
        projectVariables[index] = projectVariables[index].copy(name = name)
    }

    fun onProjectVariableTypeClick(index : Int) = viewModelScope.launch {
        navProjectEditDialogListener?.showListPickerDialog(index)
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
        navProjectEditDialogListener?.showFolderPickerDialog(
            if(projectPath.value.isNotEmpty()) projectPath.value else fileUtil.getUserDir()
        )
    }

    fun openDefaultVariablePickerDialog()  = viewModelScope.launch {
        navProjectEditDialogListener?.showDefaultVariablePickerDialog(ProjectVariable.createDefaults(osPathConverter))
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
            navProjectEditDialogListener?.showCloseWithoutSavingDialog()
        } else {
            navBackListener?.navigateBack()
        }
    }

    fun save() = viewModelScope.launch {
        var newProject = Project(
            id = project?.id ?: "",
            name = projectName.value,
            projectPath = projectPath.value,
            variable = projectVariables.toList()
        )

        project = projectRepo.updateProject(newProject)
    }

    fun delete() = viewModelScope.launch {
        project?.let {
            projectRepo.deleteProject(it.id)
            navBackListener?.navigateBack()
        }
    }

    fun closeWithoutSaving() {
        navBackListener?.navigateBack()
    }
}