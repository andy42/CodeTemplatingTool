package com.jaehl.codeTool.ui.dialog.warningDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.Logger

class WarningDialogComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val config : WarningDialogConfig
) : Component, ComponentContext by componentContext {

    private val viewModel = WarningViewModel(
        logger,
        config.declineCallBack,
        config.acceptCallBack,
    )

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
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