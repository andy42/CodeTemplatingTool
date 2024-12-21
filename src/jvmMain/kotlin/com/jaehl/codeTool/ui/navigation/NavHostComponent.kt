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
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.di.DaggerAppComponent
import com.jaehl.codeTool.ui.page.templateApply.TemplateApplyPageComponent
import com.jaehl.codeTool.ui.page.projectEdit.ProjectEditComponent
import com.jaehl.codeTool.ui.page.projectList.ProjectListComponent
import com.jaehl.codeTool.ui.page.templateEdit.TemplateEditComponent
import com.jaehl.codeTool.ui.page.templateList.TemplateListComponent

interface NavBackListener {
    fun navigateBack()
}

interface NavProjectListener {
    fun openProjectEdit(project : Project?)
}

interface NavTemplateListener {
    fun openTemplateEdit(template : Template?)
    fun openTemplateList()
    fun openTemplateApply(project : Project)
}
class NavHostComponent(
    componentContext: ComponentContext,
) : Component,
    ComponentContext by componentContext,
    NavBackListener,
    NavProjectListener,
    NavTemplateListener
{

    private val appComponent: AppComponent = DaggerAppComponent.create()

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
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                navTemplateListener = this,
                project = screenConfig.project
            )
            is ScreenConfig.ProjectEdit -> ProjectEditComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                project = screenConfig.project
            )
            is ScreenConfig.ProjectList -> ProjectListComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                navProjectListener = this,
                navTemplateListener = this
            )
            is ScreenConfig.TemplateList -> TemplateListComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                navTemplateListener = this
            )
            is ScreenConfig.TemplateEdit -> TemplateEditComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                template = screenConfig.template
            )
        }
    }

    override fun navigateBack() {
        navigation.pop()
    }

    override fun openProjectEdit(project: Project?) {
        navigation.push(ScreenConfig.ProjectEdit(project))
    }

    override fun openTemplateEdit(template: Template?) {
        navigation.push(ScreenConfig.TemplateEdit(template))
    }

    override fun openTemplateList() {
        navigation.push(ScreenConfig.TemplateList)
    }

    override fun openTemplateApply(project: Project) {
        navigation.push(ScreenConfig.TemplateApply(project))
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