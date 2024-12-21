package com.jaehl.codeTool.ui.page.projectList

import androidx.compose.runtime.mutableStateListOf
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.navigation.NavProjectListener
import com.jaehl.codeTool.ui.navigation.NavTemplateListener
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProjectListViewModel @Inject constructor(
    private val logger : Logger,
    private val projectRepo : ProjectRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navProjectListener : NavProjectListener? = null
    var navTemplateListener : NavTemplateListener? = null
    var projects = mutableStateListOf<Project>()
        private set

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)
        viewModelScope.launch {
            projectRepo.getProjects().collect { projectList ->
                projects.postSwap(projectList)
            }
        }
    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    fun onProjectSelectClick(project : Project) = viewModelScope.launch {
        navTemplateListener?.openTemplateApply(project)
    }

    fun onProjectAddClick() = viewModelScope.launch {
        navProjectListener?.openProjectEdit(null)
    }
    fun onProjectEditClick(project : Project) = viewModelScope.launch {
        navProjectListener?.openProjectEdit(project)
    }

    fun onTemplatesEditClick() = viewModelScope.launch {
        navTemplateListener?.openTemplateList()
    }
}