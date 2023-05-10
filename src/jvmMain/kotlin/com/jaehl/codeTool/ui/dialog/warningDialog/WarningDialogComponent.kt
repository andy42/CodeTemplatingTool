package com.jaehl.codeTool.ui.dialog.warningDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.Logger
import javax.inject.Inject

class WarningDialogComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val config : WarningDialogConfig
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : WarningViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, config)
        }

        WarningDialog(
            viewModel = viewModel,
            title = config.title,
            message = config.message,
            acceptText = config.acceptText,
            declineText = config.declineText
        )
    }
}