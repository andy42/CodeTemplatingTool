package com.jaehl.codeTool.ui.page.projectList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.navigation.NavProjectListener
import com.jaehl.codeTool.ui.navigation.NavTemplateListener
import com.jaehl.codeTool.util.Logger
import javax.inject.Inject

class ProjectListComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    navBackListener : NavBackListener,
    navProjectListener : NavProjectListener,
    navTemplateListener : NavTemplateListener
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : ProjectListViewModel

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navProjectListener = navProjectListener
        viewModel.navTemplateListener = navTemplateListener
    }
    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        ProjectListPage(
            viewModel = viewModel
        )
    }
}