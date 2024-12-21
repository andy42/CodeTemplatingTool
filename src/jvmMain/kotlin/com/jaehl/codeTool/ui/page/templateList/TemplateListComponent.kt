package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.navigation.NavProjectListener
import com.jaehl.codeTool.ui.navigation.NavTemplateListener
import com.jaehl.codeTool.util.Logger
import javax.inject.Inject

class TemplateListComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    navBackListener : NavBackListener,
    navTemplateListener : NavTemplateListener
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : TemplateListViewModel

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navTemplateListener = navTemplateListener
    }
    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        TemplateListPage(
            viewModel = viewModel
        )
    }
}