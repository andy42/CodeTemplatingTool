package com.jaehl.codeTool.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jaehl.codeTool.ui.R

@Composable
fun AppBar(
    title: String,
    returnButton: Boolean = false,
    onBackClick : () -> Unit
) {
    var navigationIcon : @Composable (() -> Unit)? = null
    if(returnButton) navigationIcon = {
        IconButton(content = {
            Icon(Icons.Outlined.ArrowBack, "back", tint = R.Color.TopAppBar.text)
        }, onClick = {
            onBackClick()
        })
    }

    TopAppBar(
        title = {
            Text(title, color = R.Color.TopAppBar.text)
        },
        navigationIcon = navigationIcon,
        backgroundColor = R.Color.TopAppBar.background,
        actions = {

        }
    )
}