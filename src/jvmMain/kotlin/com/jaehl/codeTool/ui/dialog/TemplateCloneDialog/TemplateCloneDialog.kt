package com.jaehl.codeTool.ui.dialog.TemplateCloneDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.DialogTitleBar
import com.jaehl.codeTool.ui.component.StyledOutlinedTextField
import com.jaehl.codeTool.ui.component.TextFieldValue

@Composable
fun TemplateCloneDialog(
    config : TemplateCloneDialogConfig,
    newTemplateName : TextFieldValue,
    onNewTemplateNameChange : (String) -> Unit,
    onCloneClick : () -> Unit
) {
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
                title = "Clone"
            )
            StyledOutlinedTextField(
                modifier = Modifier
                    .padding(top = 20.dp),
                textFieldValue = newTemplateName,
                label = { Text("New Template Name") },
                onValueChange = onNewTemplateNameChange
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
                        onCloneClick()
                    }
                ) {
                    Text(text = "Clone")
                }

                OutlinedButton(
                    modifier = Modifier
                        .padding(start = 20.dp),
                    onClick = {
                        config.onClose()
                    }
                ) {
                    Text(text = "Close")
                }

            }
        }
    }
}