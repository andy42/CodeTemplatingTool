package com.jaehl.codeTool.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jaehl.codeTool.ui.R

@Composable
fun DialogTitleBar(title: String, onClose : (() -> Unit)? = null){
    var navigationIcon : @Composable (() -> Unit)? = null
    TopAppBar(
        title = {
            Text(title, color = R.Color.TopAppBar.text)
        },
        navigationIcon = navigationIcon,
        backgroundColor = R.Color.TopAppBar.background,
        actions = {
            if(onClose != null) {
                IconButton(content = {
                    Icon(Icons.Outlined.Close, "Close", tint = R.Color.TopAppBar.text)
                }, onClick = {
                    onClose?.invoke()
                })
            }
        }
    )
}