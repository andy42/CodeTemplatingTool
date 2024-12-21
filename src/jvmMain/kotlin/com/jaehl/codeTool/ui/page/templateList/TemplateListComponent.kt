package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.dialog.TemplateCloneDialog.TemplateCloneComponent
import com.jaehl.codeTool.ui.dialog.TemplateCloneDialog.TemplateCloneDialogConfig
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.ui.navigation.NavBackListener
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

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navTemplateListener = navTemplateListener
    }

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when(config) {
                is DialogConfig.CloneTemplateConfig -> {
                    TemplateCloneComponent(
                        appComponent = appComponent,
                        componentContext = componentContext,
                        config = TemplateCloneDialogConfig(
                            requestId = config.requestId,
                            templateId = config.templateId,
                            onCreated = { requestId, newTemplate ->
                                viewModel.onTemplateClone(newTemplate)
                                dialogNavigation.dismiss()
                            },
                            onClose = {
                                dialogNavigation.dismiss()
                            }
                        )
                    )
                }
            }
        }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(lifecycle.state) {
            viewModel.init(scope)
        }

        TemplateListPage(
            viewModel = viewModel,
            onCloneTemplateClick = { template ->
                dialogNavigation.activate(DialogConfig.CloneTemplateConfig(
                    requestId = "",
                    templateId = template.id
                ))
            }
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    private sealed class DialogConfig : Parcelable {
        data class CloneTemplateConfig(val requestId: String, val templateId: String) : DialogConfig()
    }
}