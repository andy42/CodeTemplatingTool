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
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.dialog.ListPicker.ListPickerComponent
import com.jaehl.codeTool.ui.dialog.folderPicker.FolderPickerDialogComponent
import com.jaehl.codeTool.ui.dialog.folderPicker.FolderPickerDialogConfig
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.navigation.NavTemplateListener
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import javax.inject.Inject

interface NavProjectEditDialogListener {
    fun showFolderPickerDialog(currentPath : String?)
    fun showListPickerDialog(index : Int)
    fun showDefaultVariablePickerDialog(defaultVariable : List<ProjectVariable>)
    fun showCloseWithoutSavingDialog()

}
class ProjectEditComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    navBackListener : NavBackListener,
    private val project : Project?,

) : Component,
    ComponentContext by componentContext,
    NavProjectEditDialogListener{

    @Inject
    lateinit var viewModel : ProjectEditViewModel

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navProjectEditDialogListener = this
    }

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when(config) {
                is DialogConfig.FolderPickerConfig -> {
                    FolderPickerDialogComponent(
                        appComponent = appComponent,
                        componentContext = componentContext,
                        folderPickerDialogConfig = FolderPickerDialogConfig(
                            requestId = config.requestId,
                            onDismissed = {
                                dialogNavigation.dismiss()
                            },
                            startPath = config.currentPath,
                            onSelect = { requestId, path ->
                                viewModel.onProjectPathChange(requestId, path)
                            },
                            foldersOnly = true
                        )
                    )
                }

                is DialogConfig.TemplateVariableTypePickerConfig -> {
                    ListPickerComponent<TemplateVariableType>(
                        componentContext = componentContext,
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
                        requestId = config.requestId,
                        onDismissed = {
                            dialogNavigation.dismiss()
                        },
                        title = "Template Variable Type",
                        list = config.defaultVariable,
                        rowTitle = { it.name },
                        onSelect = { requestId, selected ->
                            viewModel.onAddDefaultVariable(selected)
                            dialogNavigation.dismiss()
                        }
                    )
                }
                is DialogConfig.SaveWarningConfig -> {
                    WarningDialogComponent(
                        appComponent = appComponent,
                        componentContext = componentContext,
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
                    )
                }
            }

        }

    override fun showFolderPickerDialog(currentPath : String?) {
        dialogNavigation.activate(DialogConfig.FolderPickerConfig(requestId = "", currentPath = currentPath))
    }

    override fun showListPickerDialog(index : Int) {
        dialogNavigation.activate(DialogConfig.TemplateVariableTypePickerConfig(requestId = index.toString()))
    }

    override fun showDefaultVariablePickerDialog(defaultVariable : List<ProjectVariable>) {
        dialogNavigation.activate(DialogConfig.DefaultVariablePickerConfig(requestId = "", defaultVariable = defaultVariable))
    }

    override fun showCloseWithoutSavingDialog() {
        dialogNavigation.activate(DialogConfig.SaveWarningConfig)
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel, project) {
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
        data class  DefaultVariablePickerConfig(val requestId: String, val defaultVariable : List<ProjectVariable>) : DialogConfig()
        object SaveWarningConfig : DialogConfig()
    }
}