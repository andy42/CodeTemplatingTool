package com.jaehl.codeTool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavRowTitle(
    modifier: Modifier,
    title : String,
    iconBitmap : ImageBitmap?,
    selected : Boolean,
    onClick : () -> Unit
) {
    var hover by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .background(
                if (selected) R.Color.rowSelectedBackground
                else if (hover) R.Color.rowHoverBackground
                else R.Color.rowBackground
            )
            .onPointerEvent(PointerEventType.Enter) {
                hover = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                hover = false
            }
            .clickable {
                onClick()

            }
            .padding(top = 5.dp, bottom = 5.dp)
    ) {
        if(iconBitmap != null) {
            Image(
                bitmap = iconBitmap,
                "",
                colorFilter = ColorFilter.tint(
                    if (selected) R.Color.rowSelectedText
                    else if (hover) R.Color.rowHoverText
                    else R.Color.rowText
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .width(20.dp)
                    .height(20.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()

                .padding(start = 10.dp, top = 3.dp, bottom = 3.dp),
            color =
            if (selected) R.Color.rowSelectedText
            else if (hover) R.Color.rowHoverText
            else R.Color.rowText,
            maxLines = 1
        )
    }
}