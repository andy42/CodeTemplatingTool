package com.jaehl.codeTool.ui.page.projectList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.Logger

class ProjectListComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val projectRepo : ProjectRepo,
    private val onProjectSelected: (project : Project) -> Unit,
    private val onProjectEdit : (project : Project?) -> Unit,
    private val onGoBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = ProjectListViewModel(logger, projectRepo, onProjectSelected, onProjectEdit)

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        ProjectListPage(
            viewModel = viewModel,
            onGoBackClicked = onGoBackClicked
        )
    }
}