package com.jaehl.codeTool.ui.dialog.TemplateCloneDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.navigation.Component
import javax.inject.Inject

data class TemplateCloneDialogConfig(
    val requestId : String,
    val templateId : String,
    val onCreated : ((requestId : String, newTemplate : Template?) -> Unit)? = null,
    val onClose : () -> Unit
)

class TemplateCloneComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val config : TemplateCloneDialogConfig
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : TemplateCloneViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()

        LaunchedEffect(viewModel) {
            viewModel.init(scope, config.templateId)
        }

        LaunchedEffect(viewModel.uiEvent){
            val uiEvent = viewModel.uiEvent
            when(uiEvent){
                is TemplateCloneViewModel.UiEvent.Empty -> return@LaunchedEffect
                is TemplateCloneViewModel.UiEvent.TemplateCloned -> {
                    config.onCreated?.invoke(config.requestId, uiEvent.template)
                }
            }
            viewModel.clearUiEvent()
        }

        TemplateCloneDialog(
            config = config,
            newTemplateName = viewModel.newTemplateName,
            onNewTemplateNameChange = viewModel::onNewTemplateNameChange,
            onCloneClick = {
                viewModel.onCloneClick()
            }
        )
    }
}