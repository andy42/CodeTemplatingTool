package com.jaehl.codeTool.ui.page.projectList

import androidx.compose.runtime.mutableStateListOf
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProjectListViewModel(
    private val logger : Logger,
    private val projectRepo : ProjectRepo,
    private val onProjectSelected: (project : Project) -> Unit,
    private val onProjectEdit : (project : Project?) -> Unit,
) : ViewModel() {

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

    fun onProjectSelectClick(project : Project) = viewModelScope.launch {
        onProjectSelected(project)
    }

    fun onProjectAddClick() = viewModelScope.launch {
        onProjectEdit(null)
    }
    fun onProjectEditClick(project : Project) = viewModelScope.launch {
        onProjectEdit(project)
    }
}