package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar

@Composable
fun TemplateEditPage(
    viewModel : TemplateEditViewModel,
    onGoBackClicked: () -> Unit
) {
    Column(modifier = Modifier.background(R.Color.pageBackground)) {
        AppBar(
            title = "TemplateEdit",
            returnButton = true,
            onBackClick = {
                onGoBackClicked()
            }
        )
        Row(
            modifier = Modifier
        ) {
            NavPannel(viewModel)

            val navRowFileSelect = (viewModel.selectedNavRow.value as? TemplateEditViewModel.NavRowSelect.NavRowFileSelect)

            if(viewModel.selectedNavRow.value is TemplateEditViewModel.NavRowSelect.NavRowGeneralInfoSelect) {
                MainPannel(viewModel)
            }
            else if (navRowFileSelect != null){
                FilePannel(
                    viewModel = viewModel,
                    templateFile = viewModel.files[navRowFileSelect.index]
                )
            }
        }
    }
}

@Composable
fun NavPannel(
    viewModel : TemplateEditViewModel
) {
    val fileBitmap = remember { useResource("file.png") { loadImageBitmap(it) } }
    Column(
        modifier = Modifier.width(250.dp)
    ) {
        NavRowTitle(
            title = "General Info",
            iconBitmap = null,
            selected = (viewModel.selectedNavRow.value is TemplateEditViewModel.NavRowSelect.NavRowGeneralInfoSelect),
            onClick = {
                viewModel.onGeneralInfoClick()
            }
        )

        NavRowHeading("Files")

        viewModel.files.forEachIndexed { index, file ->
            val navRowFileSelect = (viewModel.selectedNavRow.value as? TemplateEditViewModel.NavRowSelect.NavRowFileSelect)
            NavRowTitle(
                title = file.name,
                iconBitmap = fileBitmap,
                selected = (navRowFileSelect?.index == index),
                onClick = {
                    viewModel.onTemplateClick(index)
                }
            )
        }
    }
}

@Composable
fun NavRowHeading(
    title : String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(R.Color.transparent)
                .padding(top = 3.dp, bottom = 3.dp),
            color = R.Color.textDark,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavRowTitle(
    title : String,
    iconBitmap : ImageBitmap?,
    selected : Boolean,
    onClick : () -> Unit
) {
    var active by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()

            .background(
                if (selected) R.Color.rowActiveBackground
                else if (active) R.Color.rowHoverBackground
                else R.Color.transparent
            )
            .onPointerEvent(PointerEventType.Enter) {
                active = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                active = false
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
                    if (selected) R.Color.textLight
                    else if (active) R.Color.textDark
                    else R.Color.textDark
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
                if (selected) R.Color.textLight
                else if (active) R.Color.textDark
                else R.Color.textDark,
            maxLines = 1
        )
    }
}

@Composable
fun MainPannel(
    viewModel : TemplateEditViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = viewModel.name.value,
            onValueChange = {
                viewModel.onNameChange(it)
                            },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        )
    }
}

@Composable
fun FilePannel(
    viewModel : TemplateEditViewModel,
    templateFile : TemplateFileViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = templateFile.path,
            onValueChange = {
                //viewModel.onNameChange(it)
            },
            label = { Text("Path") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        )

        OutlinedTextField(
            value = templateFile.pathDestination,
            onValueChange = {
                viewModel.onNameChange(it)
            },
            label = { Text("Path Destination") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        )
    }
}

