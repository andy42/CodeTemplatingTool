package com.jaehl.codeTool.ui.page.templateApply

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
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.data.templateCreator.TemplateCreator
import com.jaehl.codeTool.data.templateParser.TemplateParser
import com.jaehl.codeTool.ui.dialog.folderPicker.FolderPickerDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger

class TemplateApplyPageComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
    private val templateParser: TemplateParser,
    private val templateCreator : TemplateCreator,
    private val project : Project,
    private val onGoBackClicked: () -> Unit,
    private val onOpenTemplateList: () -> Unit
) : Component, ComponentContext by componentContext {

    private val viewModel = TemplateApplyViewModel(logger, fileUtil, templateParser, templateCreator, templateRepo, ::showFolderPickerDialog, ::showWarningDialog)
    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when(config) {
                is DialogConfig.PackagePickerConfig -> {
                    FolderPickerDialogComponent(
                        componentContext = componentContext,
                        logger = logger,
                        fileUtil = fileUtil,
                        requestId = config.requestId,
                        onDismissed = {
                            dialogNavigation.dismiss()
                        },
                        subPathOnly = true,
                        startPath = config.startPath,
                        onSelect = { requestId, path ->
                            viewModel.onProjectPathChange(requestId, path.replace(config.startPath, ""))
                        },
                        foldersOnly = true
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
            viewModel.init(scope, project)
        }

        HomePage(
            viewModel = viewModel,
            onGoBackClicked = onGoBackClicked,
            onOpenTemplateList = onOpenTemplateList
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    fun showFolderPickerDialog(requestId : String, startPath : String) {
        dialogNavigation.activate(DialogConfig.PackagePickerConfig(requestId = requestId, startPath = startPath))
    }

    fun showWarningDialog (warningDialogConfig : WarningDialogConfig) {

        dialogNavigation.activate(
            DialogConfig.WarningDialog(
                warningDialogConfig
            )
        )
    }

    private sealed class DialogConfig : Parcelable {
        data class PackagePickerConfig(val requestId: String, val startPath: String) : DialogConfig()
        data class WarningDialog(
            val warningDialogConfig : WarningDialogConfig
        ) : DialogConfig()
    }
}