package com.jaehl.codeTool.ui.page.projectEdit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.overlay.*
import com.arkivanov.essenty.parcelable.Parcelable
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.ProjectVariable
import com.jaehl.codeTool.data.model.TemplateVariableType

import com.jaehl.codeTool.data.repo.ProjectRepo
import com.jaehl.codeTool.ui.dialog.ListPicker.ListPickerComponent
import com.jaehl.codeTool.ui.dialog.folderPicker.FolderPickerDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger

class ProjectEditComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val osPathConverter : OsPathConverter,
    private val projectRepo : ProjectRepo,
    private val fileUtil : FileUtil,
    private val project : Project?,
    private val onGoBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = ProjectEditViewModel(
        logger,
        projectRepo,
        project,
        ::showFolderPickerDialog,
        ::showListPickerDialog,
        ::showDefaultVariablePickerDialog,
        onGoBackClicked,
        ::showCloseWithoutSavingDialog)

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when(config) {
                is DialogConfig.FolderPickerConfig -> {
                    FolderPickerDialogComponent(
                        componentContext = componentContext,
                        logger = logger,
                        fileUtil = fileUtil,
                        requestId = config.requestId,
                        onDismissed = {
                            dialogNavigation.dismiss()
                        },
                        startPath = config.currentPath ?: fileUtil.getUserDir(),
                        onSelect = { requestId, path ->
                            viewModel.onProjectPathChange(requestId, path)
                        },
                        foldersOnly = true
                    )
                }

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
                            viewModel.onProjectVariableTypeChange(requestId.toInt(), selected)
                            dialogNavigation.dismiss()
                        }
                    )
                }
                is DialogConfig.DefaultVariablePickerConfig -> {
                    ListPickerComponent<ProjectVariable>(
                        componentContext = componentContext,
                        logger = logger,
                        requestId = config.requestId,
                        onDismissed = {
                            dialogNavigation.dismiss()
                        },
                        title = "Template Variable Type",
                        list = ProjectVariable.createDefaults(osPathConverter),
                        rowTitle = { it.name },
                        onSelect = { requestId, selected ->
                            viewModel.onAddDefaultVariable(selected)
                            dialogNavigation.dismiss()
                        }
                    )
                }
                is DialogConfig.SaveWarningConfig -> {
                    WarningDialogComponent(
                        componentContext = componentContext,
                        logger = logger,
                        config = WarningDialogConfig(
                            title = "Warning",
                            message = "Do you want to close without saving?",
                            acceptText = "Yes",
                            declineText = "No",
                            acceptCallBack = {
                                dialogNavigation.dismiss()
                                viewModel.closeWithoutSaving()
                            },
                            declineCallBack = {
                                dialogNavigation.dismiss()
                            }
                        )
//                        requestId = "",
//                        title = "Warning",
//                        message = "Do you want to close without saving?",
//                        acceptText = "Yes",
//                        declineText = "No",
//                        onClose = {
//                            dialogNavigation.dismiss()
//                        },
//                        onAccept = { requestId ->
//                            dialogNavigation.dismiss()
//                            viewModel.closeWithoutSaving()
//                        }
                    )
                }
            }

        }

    fun showFolderPickerDialog(currentPath : String?) {
        dialogNavigation.activate(DialogConfig.FolderPickerConfig(requestId = "", currentPath = currentPath))
    }

    fun showListPickerDialog(index : Int) {
        dialogNavigation.activate(DialogConfig.TemplateVariableTypePickerConfig(requestId = index.toString()))
    }

    fun showDefaultVariablePickerDialog() {
        dialogNavigation.activate(DialogConfig.DefaultVariablePickerConfig(requestId = ""))
    }

    fun showCloseWithoutSavingDialog() {
        dialogNavigation.activate(DialogConfig.SaveWarningConfig)
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        ProjectEditPage(
            viewModel = viewModel
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    private sealed class DialogConfig : Parcelable {
        data class FolderPickerConfig(val requestId: String, val currentPath : String?) : DialogConfig()
        data class  TemplateVariableTypePickerConfig(val requestId: String) : DialogConfig()
        data class  DefaultVariablePickerConfig(val requestId: String) : DialogConfig()
        object SaveWarningConfig : DialogConfig()
    }
}