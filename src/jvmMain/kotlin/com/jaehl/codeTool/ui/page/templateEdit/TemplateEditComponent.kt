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
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.dialog.ListPicker.ListPickerComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogComponent
import com.jaehl.codeTool.ui.dialog.warningDialog.WarningDialogConfig
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import javax.inject.Inject

interface NavTemplateEditDialogListener {
    fun showTypeVariablePickerDialog(index : Int)
    fun showWarningDialog(warningDialogConfig : WarningDialogConfig)
}
class TemplateEditComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    navBackListener : NavBackListener,
    private val template : Template?
) : Component,
    ComponentContext by componentContext,
    NavTemplateEditDialogListener{

    @Inject
    lateinit var viewModel : TemplateEditViewModel

    @Inject
    lateinit var logger : Logger

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navTemplateEditDialogListener = this
    }

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
                        appComponent = appComponent,
                        componentContext = componentContext,
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
            viewModel.init(scope, template)
        }

        TemplateEditPage(
            viewModel = viewModel
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    override fun showTypeVariablePickerDialog(index : Int) {
        dialogNavigation.activate(DialogConfig.TemplateVariableTypePickerConfig(requestId = index.toString()))
    }

    override fun showWarningDialog (warningDialogConfig : WarningDialogConfig) {

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