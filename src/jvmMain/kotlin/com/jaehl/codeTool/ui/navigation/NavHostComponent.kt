package com.jaehl.codeTool.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import com.arkivanov.essenty.parcelable.Parcelable
import com.google.gson.reflect.TypeToken
import com.jaehl.codeTool.Configuration
import com.jaehl.codeTool.ConfigurationImp
import com.jaehl.codeTool.data.local.*
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.data.templateCreator.TemplateCreator
import com.jaehl.codeTool.data.templateCreator.TemplateCreatorImp
import com.jaehl.codeTool.data.templateParser.TemplateParser
import com.jaehl.codeTool.data.templateParser.TemplateParserImp
import com.jaehl.codeTool.ui.page.home.HomePageComponent
import com.jaehl.codeTool.ui.page.projectList.ProjectListComponent
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditComponent
import com.jaehl.codeTool.ui.page.templateList.TemplateListComponent
import com.jaehl.codeTool.ui.page.templateList.TemplateListPage
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.FileUtilImp
import com.jaehl.codeTool.util.Logger

class NavHostComponent(
    componentContext: ComponentContext,
) : Component, ComponentContext by componentContext {

    private val configuration : Configuration = ConfigurationImp()
    private val logger : Logger = Logger()
    private val fileUtil: FileUtil = FileUtilImp()
    private val templateParser: TemplateParser = TemplateParserImp(fileUtil, logger)

    private val templateListFile : TemplateListFile = TemplateListFileImp(logger)

    private val templateListLoader : ObjectListLoader<Template> = ObjectListJsonLoader(
        logger = logger,
        type = object : TypeToken<Array<Template>>() {}.type,
        projectUserDir = configuration.getProjectUserDir(),
        templateListFile = configuration.getTemplateListFile())

    private val templateRepo : TemplateRepo = TemplateRepo(logger, templateListLoader)

    private val projectListLoader : ObjectListLoader<Project> = ObjectListJsonLoader(
        logger = logger,
        type = object : TypeToken<Array<Project>>() {}.type,
        projectUserDir = configuration.getProjectUserDir(),
        templateListFile = configuration.getProjectListFile())

    private val projectRepo : ProjectRepo = ProjectRepo(logger, projectListLoader)

    private val templateCreator : TemplateCreator = TemplateCreatorImp(fileUtil, logger)

    private val router = router<ScreenConfig, Component>(
        initialConfiguration = ScreenConfig.ProjectList,
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            is ScreenConfig.Home -> HomePageComponent(
                componentContext,
                logger,
                fileUtil,
                templateRepo,
                templateParser,
                templateCreator,
                screenConfig.project,
                ::onGoBackClicked,
                ::onOpenTemplateList
            )
            is ScreenConfig.ProjectList -> ProjectListComponent(
                componentContext,
                logger,
                projectRepo,
                ::onProjectSelected,
                ::onGoBackClicked
            )
            is ScreenConfig.TemplateList -> TemplateListComponent(
                componentContext,
                logger,
                templateRepo,
                ::onGoBackClicked,
                ::onOpenTemplateEdit
            )
            is ScreenConfig.TemplateEdit -> TemplateEditComponent(
                componentContext,
                logger,
                fileUtil,
                templateRepo,
                ::onGoBackClicked
            )
        }
    }

    private fun onOpenTemplateEdit(template : Template){
        router.push(ScreenConfig.TemplateEdit(template))
    }
    private fun onOpenTemplateList(){
        router.push(ScreenConfig.TemplateList)
    }
    private fun onProjectSelected(project : Project){
        router.push(ScreenConfig.Home(project))
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
        data class Home(val project: Project) : ScreenConfig()
        object ProjectList : ScreenConfig()
        object TemplateList : ScreenConfig()

        data class TemplateEdit(val template: Template) : ScreenConfig()
    }
}