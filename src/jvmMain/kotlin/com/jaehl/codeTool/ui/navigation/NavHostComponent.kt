package com.jaehl.codeTool.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.router
import com.arkivanov.essenty.parcelable.Parcelable
import com.jaehl.codeTool.data.local.TemplateListFile
import com.jaehl.codeTool.data.local.TemplateListFileImp
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.data.templateCreator.TemplateCreator
import com.jaehl.codeTool.data.templateCreator.TemplateCreatorImp
import com.jaehl.codeTool.data.templateParser.TemplateParser
import com.jaehl.codeTool.data.templateParser.TemplateParserImp
import com.jaehl.codeTool.ui.page.home.HomePageComponent
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.FileUtilImp
import com.jaehl.codeTool.util.Logger

class NavHostComponent(
    componentContext: ComponentContext,
) : Component, ComponentContext by componentContext {

    private val logger : Logger = Logger()
    private val fileUtil: FileUtil = FileUtilImp()
    private val templateParser: TemplateParser = TemplateParserImp(fileUtil, logger)
    private val templateListFile : TemplateListFile = TemplateListFileImp(logger)
    private val templateRepo : TemplateRepo = TemplateRepo(logger, templateListFile)
    private val templateCreator : TemplateCreator = TemplateCreatorImp(fileUtil, logger)

    private val router = router<ScreenConfig, Component>(
        initialConfiguration = ScreenConfig.Home,
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            ScreenConfig.Home -> HomePageComponent(
                componentContext,
                logger,
                fileUtil,
                templateRepo,
                templateParser,
                templateCreator,
                ::onGoBackClicked
            )
        }
    }

    private fun onGoBackClicked() {
        router.pop()
    }

    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    override fun render() {
        Children(routerState = router.state) {
            it.instance.render()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        object Home : ScreenConfig()
    }
}