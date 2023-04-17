package com.jaehl.codeTool.ui.dialog.ListPicker

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.DialogTitleBar

@Composable
fun <T> ListPickerDialog(
    requestId : String,
    title: String,
    list : List<T>,
    rowTitle : (value : T) -> String,
    onSelect : (requestId : String, item : T) -> Unit,
    onClose : () -> Unit,
    dialogWidth : Dp?,
    dialogHeight : Dp?
){
    val state : LazyListState = rememberLazyListState()
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .clickable {}
        .background(R.Color.dialogBackground)) {
        Column(
            modifier = Modifier
                .width(dialogWidth ?: 400.dp)
                .height(dialogHeight ?: 400.dp)
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.Center)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogTitleBar(
                title = title,
                onClose = onClose
            )
            Box(Modifier.fillMaxWidth()) {
                LazyColumn(
                    state = state
                ) {
                    itemsIndexed(list) { index, item ->
                        ItemPickerRow(
                            index = index,
                            title = rowTitle(item),
                            item = item,
                            onSelect = {
                                onSelect(requestId, it)
                            }
                        )
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }
}

@Composable
fun <T> ItemPickerRow(
    index : Int,
    title : String,
    item : T,
    onSelect : (item : T) -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(item)
            }
            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier
        )
    }

}