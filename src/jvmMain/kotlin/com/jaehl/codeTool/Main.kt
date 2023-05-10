package com.jaehl.codeTool

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.jaehl.codeTool.ui.navigation.NavHostComponent
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.jaehl.codeTool.ui.R

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
fun main() = application {
    val windowState = rememberWindowState(width = 1100.dp, height = 1300.dp)

    Window(onCloseRequest = ::exitApplication, state = windowState) {
        MaterialTheme(
            colors = R.lightColors
        ) {
            remember {
                NavHostComponent(
                    DefaultComponentContext(
                        LifecycleRegistry(),
                    )
                )
            }.render()
        }
    }
}

