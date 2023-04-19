package com.jaehl.codeTool.ui.page.templateEdit

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
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateVariableType
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.ui.dialog.ListPicker.ListPickerComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger

class TemplateEditComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
    private val templateEditValidator : TemplateEditValidator,
    private val template : Template?,
    private val onClose: () -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = TemplateEditViewModel(
        logger,
        fileUtil,
        templateRepo,
        templateEditValidator,
        template,
        showTypeVariablePickerDialog = ::showTypeVariablePickerDialog,
        onClose = onClose,
        showWarningDialog = ::showWarningDialog)

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when(config) {
                is DialogConfig.TemplateVariableTypePickerConfig -> {
                    ListPickerComponent<TemplateVariableType>(
                        componentContext = componentContext,
                        logger = logger,
                        requestId = config.requestId,
                        onDismissed = {
                            dialogNavigation.dismiss()
                        },
                        title = "Template Variable Type",
                        list = TemplateVariableType.values().toList(),
                        rowTitle = { it.value },
                        onSelect = { requestId, selected ->
                            viewModel.onTemplateVariableTypeChange(requestId.toInt(), selected)
                            dialogNavigation.dismiss()
                        }
                    )
                }
                is DialogConfig.WarningDialog -> {
                    WarningDialogComponent(
                        componentContext = componentContext,
                        logger = logger,
                        config = config.warningDialogConfig.copy(
                            acceptCallBack = {
                                dialogNavigation.dismiss()
                                config.warningDialogConfig.acceptCallBack()
                            },
                            declineCallBack = {
                                dialogNavigation.dismiss()
                                config.warningDialogConfig.declineCallBack()
                            }
                        )
                    )
                }
            }
        }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        TemplateEditPage(
            viewModel = viewModel
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    fun showTypeVariablePickerDialog(index : Int) {
        dialogNavigation.activate(DialogConfig.TemplateVariableTypePickerConfig(requestId = index.toString()))
    }

    fun showWarningDialog (warningDialogConfig : WarningDialogConfig) {

        dialogNavigation.activate(
            DialogConfig.WarningDialog(
                warningDialogConfig
            )
        )
    }

    private sealed class DialogConfig : Parcelable {
        data class  TemplateVariableTypePickerConfig(val requestId: String) : DialogConfig()

        data class WarningDialog(
            val warningDialogConfig : WarningDialogConfig
        ) : DialogConfig()
    }
}