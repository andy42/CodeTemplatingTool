package com.jaehl.codeTool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R

@Composable
fun NavRowHeading(
    modifier: Modifier,
    title : String,
    icon : ImageVector? = null,
    iconDescription : String = "",
    onIconClick : (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(R.Color.transparent)
                .padding(top = 3.dp, bottom = 3.dp),
            color = R.Color.OnSurface.highEmphasis,
            maxLines = 1
        )
        if(icon != null) {
            IconButton(
                modifier = Modifier,
                content = {
                    Icon(icon, iconDescription, tint = R.Color.OnSurface.highEmphasis)
                }, onClick = {
                    onIconClick?.invoke()
                }
            )
        }
    }
}