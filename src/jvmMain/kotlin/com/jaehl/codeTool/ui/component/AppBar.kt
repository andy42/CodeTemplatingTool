package com.jaehl.codeTool.ui.component

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun AppBar(
    title: String,
    returnButton: Boolean = false,
    onBackClick : () -> Unit
) {
    var navigationIcon : @Composable (() -> Unit)? = null
    if(returnButton) navigationIcon = {
        IconButton(content = {
            Icon(Icons.Outlined.ArrowBack, "back", tint = MaterialTheme.colors.onPrimary)
        }, onClick = {
            onBackClick()
        })
    }

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = navigationIcon,
        actions = {
        }
    )
}