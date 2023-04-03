package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.Logger

class TemplateListComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val templateRepo : TemplateRepo,
    private val onGoBackClicked: () -> Unit,
    private val onOpenTemplateEdit: (template : Template?) -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = TemplateListViewModel(logger, templateRepo, onOpenTemplateEdit)

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        TemplateListPage(
            viewModel = viewModel,
            onGoBackClicked = onGoBackClicked
        )
    }
}