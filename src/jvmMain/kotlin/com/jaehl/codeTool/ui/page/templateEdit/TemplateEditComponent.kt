package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger

class TemplateEditComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
    private val onGoBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = TemplateEditViewModel(logger, fileUtil, templateRepo)

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        TemplateEditPage(
            viewModel = viewModel,
            onGoBackClicked = onGoBackClicked
        )
    }
}