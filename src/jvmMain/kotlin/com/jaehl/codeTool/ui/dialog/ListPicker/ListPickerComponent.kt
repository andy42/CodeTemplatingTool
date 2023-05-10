package com.jaehl.codeTool.ui.dialog.ListPicker

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.Logger

class ListPickerComponent <T>(
    private val componentContext: ComponentContext,
    private val requestId : String,
    private val onDismissed : () -> Unit,
    private val title: String,
    private val list : List<T>,
    private val rowTitle : (value : T) -> String,
    private val onSelect : (requestId : String, item : T) -> Unit,
    private val dialogWidth : Dp? = null,
    private val dialogHeight : Dp? = null
) : Component, ComponentContext by componentContext {

    @Composable
    override fun render() {

        ListPickerDialog(
            requestId = requestId,
            title = title,
            list = list,
            rowTitle = rowTitle,
            onSelect = onSelect,
            onClose = {
                onDismissed()
            },
            dialogWidth = dialogWidth,
            dialogHeight = dialogHeight
        )
    }
}