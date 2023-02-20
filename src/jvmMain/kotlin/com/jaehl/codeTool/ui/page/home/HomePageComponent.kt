package com.jaehl.codeTool.ui.page.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.data.templateCreator.TemplateCreator
import com.jaehl.codeTool.data.templateParser.TemplateParser
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger

class HomePageComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
    private val templateParser: TemplateParser,
    private val templateCreator : TemplateCreator,
    private val onGoBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = HomeViewModel(logger, fileUtil, templateParser, templateCreator, templateRepo)

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, Project.temp())
        }

        HomePage(
            viewModel = viewModel,
            onGoBackClicked = onGoBackClicked
        )
    }
}