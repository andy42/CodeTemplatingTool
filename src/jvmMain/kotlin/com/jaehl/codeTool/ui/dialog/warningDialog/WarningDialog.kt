package com.jaehl.codeTool.ui.dialog.warningDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.DialogTitleBar

@Composable
fun WarningDialog(
    viewModel : WarningViewModel,
    title : String,
    message : String,
    acceptText : String,
    declineText : String?
){
    val state : LazyListState = rememberLazyListState()
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(R.Color.dialogBackground)) {
        Column(
            modifier = Modifier
                .width(400.dp)
                //.height(400.dp)
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.Center)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogTitleBar(
                title = title
            )
            Text(
                modifier = Modifier
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                text = message,
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, bottom = 10.dp, end = 20.dp)
            ) {
                Button(
                    modifier = Modifier,
                    onClick = {
                        viewModel.onAcceptClick()
                    }
                ) {
                    Text(text = acceptText)
                }
                if(declineText != null) {
                    OutlinedButton(
                        modifier = Modifier
                            .padding(start = 20.dp),
                        onClick = {
                            viewModel.onDeclineClick()
                        }
                    ) {
                        Text(text = declineText)
                    }
                }
            }
        }
    }
}