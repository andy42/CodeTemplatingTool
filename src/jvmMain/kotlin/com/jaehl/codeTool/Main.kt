// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jaehl.codeTool

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.jaehl.codeTool.ui.navigation.NavHostComponent
import com.arkivanov.essenty.lifecycle.LifecycleRegistry


fun main() = application {
    val windowState = rememberWindowState(width = 1100.dp, height = 1300.dp)

    Window(onCloseRequest = ::exitApplication, state = windowState) {
        MaterialTheme {
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

