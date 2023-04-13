package com.jaehl.codeTool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    thickness: Dp = 1.dp
) {
    Box(
        modifier
            .fillMaxHeight()
            .width(thickness)
            .background(color = color)
    )
}
