package com.jaehl.codeTool.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Picker(
    modifier : Modifier = Modifier,
    title : String,
    value : String,
    onClick : () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { },
            label = { Text(title) }
        )
        Box(modifier = Modifier
            .matchParentSize()
            .clickable {
                onClick()
            })
    }
}