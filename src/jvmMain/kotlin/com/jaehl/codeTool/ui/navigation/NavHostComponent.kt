package com.jaehl.codeTool.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
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
import com.jaehl.codeTool.ui.page.templateApply.TemplateApplyPageComponent
import com.jaehl.codeTool.ui.page.projectEdit.ProjectEditComponent
import com.jaehl.codeTool.ui.page.projectList.ProjectListComponent
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditComponent
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditValidator
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditValidatorImp
import com.jaehl.codeTool.ui.page.templateList.TemplateListComponent
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.ui.util.OsPathConverterImp
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.FileUtilImp
import com.jaehl.codeTool.util.Logger

class NavHostComponent(
    componentContext: ComponentContext,
) : Component, ComponentContext by componentContext {

    private val configuration : Configuration = ConfigurationImp()
    private val logger : Logger = Logger()
    private val fileUtil: FileUtil = FileUtilImp(logger)
    private val templateParser: TemplateParser = TemplateParserImp(fileUtil, logger)

    private val templateListFile : TemplateListFile = TemplateListFileImp(logger)

    private val templateListLoader : ObjectListLoader<Template> = ObjectListJsonLoader(
        logger = logger,
        type = object : TypeToken<Array<Template>>() {}.type,
        projectUserDir = configuration.getProjectUserDir(),
        templateListFile = configuration.getTemplateListFile())

    private val osPathConverter : OsPathConverter = OsPathConverterImp()

    private val templateRepo : TemplateRepo = TemplateRepo(logger, templateListLoader, osPathConverter, fileUtil)

    private val templateEditValidator : TemplateEditValidator = TemplateEditValidatorImp(fileUtil, templateRepo)

    private val projectListLoader : ObjectListLoader<Project> = ObjectListJsonLoader(
        logger = logger,
        type = object : TypeToken<Array<Project>>() {}.type,
        projectUserDir = configuration.getProjectUserDir(),
        templateListFile = configuration.getProjectListFile())

    private val projectRepo : ProjectRepo = ProjectRepo(logger, projectListLoader, osPathConverter)

    private val templateCreator : TemplateCreator = TemplateCreatorImp(fileUtil, logger)

    private val navigation = StackNavigation<ScreenConfig>()

    private val _childStack =
        childStack(
            source = navigation,
            initialConfiguration = ScreenConfig.ProjectList,
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createScreenComponent,
        )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            is ScreenConfig.TemplateApply -> TemplateApplyPageComponent(
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
            is ScreenConfig.ProjectEdit -> ProjectEditComponent(
                componentContext,
                logger,
                osPathConverter,
                projectRepo,
                fileUtil,
                screenConfig.project,
                ::onGoBackClicked
            )
            is ScreenConfig.ProjectList -> ProjectListComponent(
                componentContext,
                logger,
                projectRepo,
                ::onProjectSelected,
                ::onProjectEdit,
                onTemplatesEdit = ::onOpenTemplateList,
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
                templateEditValidator,
                screenConfig.template,
                ::onGoBackClicked
            )
        }
    }

    private fun onOpenTemplateEdit(template : Template?){
        navigation.push(ScreenConfig.TemplateEdit(template))
    }
    private fun onOpenTemplateList(){
        navigation.push(ScreenConfig.TemplateList)
    }

    private fun onProjectEdit(project : Project?){
        navigation.push(ScreenConfig.ProjectEdit(project))
    }
    private fun onProjectSelected(project : Project){
        navigation.push(ScreenConfig.TemplateApply(project))
    }

    private fun onGoBackClicked() {
        navigation.pop()
    }

    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    override fun render() {
        Children(stack = _childStack, modifier = Modifier){
            it.instance.render()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        data class TemplateApply(val project: Project) : ScreenConfig()
        object ProjectList : ScreenConfig()
        object TemplateList : ScreenConfig()

        data class TemplateEdit(val template: Template?) : ScreenConfig()

        data class ProjectEdit(val project: Project?) : ScreenConfig()
    }
}