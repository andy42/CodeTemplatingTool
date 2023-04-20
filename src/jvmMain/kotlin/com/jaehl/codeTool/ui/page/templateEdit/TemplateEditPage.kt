package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.TemplateVariable
import com.jaehl.codeTool.data.model.TemplateVariableType
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.*

@Composable
fun TemplateEditPage(
    viewModel : TemplateEditViewModel
) {
    Column(modifier = Modifier.background(R.Color.pageBackground)) {
        AppBar(
            title = "TemplateEdit",
            returnButton = true,
            onBackClick = {
                viewModel.onCloseClick()
            }
        )
        Row(
            modifier = Modifier
        ) {
            NavView(viewModel)
            VerticalDivider(
                thickness = 1.dp,
                color = R.Color.dividerColor
            )

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
fun NavView(
    viewModel : TemplateEditViewModel
) {
    val fileBitmap = remember { useResource("file.png") { loadImageBitmap(it) } }
    Column(
        modifier = Modifier
            .width(250.dp)
    ) {
        NavRowTitle(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            title = "General Info",
            iconBitmap = null,
            selected = (viewModel.selectedNavRow.value is TemplateEditViewModel.NavRowSelect.NavRowGeneralInfoSelect),
            onClick = {
                viewModel.onGeneralInfoClick()
            }
        )

        NavRowHeading(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp),
            title = "Files",
            icon = Icons.Outlined.Add,
            iconDescription = "Add File",
            onIconClick = {
                viewModel.addTemplateFile()
            }
        )

        viewModel.files.forEachIndexed { index, file ->
            val navRowFileSelect = (viewModel.selectedNavRow.value as? TemplateEditViewModel.NavRowSelect.NavRowFileSelect)
            NavRowTitle(
                modifier = Modifier
                    .fillMaxWidth(),
                title = file.name,
                iconBitmap = fileBitmap,
                selected = (navRowFileSelect?.index == index),
                onClick = {
                    viewModel.onTemplateFileClick(index)
                }
            )
        }
    }
}

@Composable
fun MainPannel(
    viewModel : TemplateEditViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
            .fillMaxWidth()
            .background(R.Color.cardBackground)
            .padding(10.dp)
    ) {
        OutlinedTextField(
            value = viewModel.name.value.value,
            onValueChange = {
                viewModel.onNameChange(it)
                            },
            label = { Text("Name") },
            isError = (viewModel.name.value.error != null),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = R.Color.OutlinedTextField.focusedBorderColor,
                focusedLabelColor = R.Color.OutlinedTextField.focusedLabelColor
            )
        )
        if(viewModel.name.value.error != null) {
            Text(text = viewModel.name.value.error ?: "", color = R.Color.errorText)
        }

        viewModel.variables.forEachIndexed { index, variable ->
            TemplateVariable(
                viewModel,
                index,
                variable
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = R.Color.ButtonDelete.background,
                    contentColor = R.Color.ButtonDelete.text
                ),
                onClick = {
                    viewModel.deleteTemplate()
                }
            ) {
                Text(text = "Delete Template")
            }

            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = R.Color.Button.background,
                    contentColor = R.Color.Button.text
                ),
                onClick = {
                    viewModel.addVariable()
                }
            ) {
                Text(text = "Add Variable")
            }

            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = R.Color.Button.background,
                    contentColor = R.Color.Button.text
                ),
                enabled = (viewModel.isSaveEnabled.value),
                onClick = {
                    viewModel.save()
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun TemplateVariable(
    viewModel : TemplateEditViewModel,
    index : Int,
    templateVariable : TemplateVariable,
) {
    Column(
        modifier = Modifier
            .padding(top = 40.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(R.Color.Card.SubTitle.background)
        ) {
            Text(
                text = if(templateVariable.name.isEmpty()) "Variable ${index +1}" else templateVariable.name,
                color = R.Color.Card.SubTitle.text,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp)
            )
            IconButton(
                modifier = Modifier,
                content = {
                    Icon(Icons.Outlined.Delete, "Delete", tint = R.Color.Card.SubTitle.text)
                }, onClick = {
                    viewModel.onTemplateVariableDelete(index)
                }
            )
        }
        OutlinedTextField(
            value = templateVariable.name,
            onValueChange = {
                viewModel.onTemplateVariableNameChange(index, it)
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = R.Color.OutlinedTextField.focusedBorderColor,
                focusedLabelColor = R.Color.OutlinedTextField.focusedLabelColor
            )
        )

        Picker(
            title = "Type",
            value = templateVariable.type.value,
            onClick = {
                viewModel.onTemplateVariableTypeClick(index)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )

        if(templateVariable.type.value == TemplateVariableType.Path.value){
            OutlinedTextField(
                value = templateVariable.startPath,
                onValueChange = {
                    viewModel.onTemplateVariableStartPathChange(index, it)
                },
                label = { Text("Start Path") },
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = R.Color.OutlinedTextField.focusedBorderColor,
                    focusedLabelColor = R.Color.OutlinedTextField.focusedLabelColor
                )
            )
        }

        OutlinedTextField(
            value = templateVariable.default,
            onValueChange = {
                viewModel.onTemplateVariableDefaultChange(index, it)
            },
            label = { Text("Default") },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = R.Color.OutlinedTextField.focusedBorderColor,
                focusedLabelColor = R.Color.OutlinedTextField.focusedLabelColor
            )
        )
    }
}

@Composable
fun FilePannel(
    viewModel : TemplateEditViewModel,
    templateFile : TemplateFileViewModel
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .background(R.Color.cardBackground)
            .padding(10.dp)
    ) {
        OutlinedTextField(
            value = viewModel.templateFilePath.value.value,
            onValueChange = {
                viewModel.onTemplateFilePathChange(it)
            },
            label = { Text("Path") },
            isError = (viewModel.templateFilePath.value.error != null),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = R.Color.OutlinedTextField.focusedBorderColor,
                focusedLabelColor = R.Color.OutlinedTextField.focusedLabelColor
            )
        )
        if(viewModel.templateFilePath.value.error != null) {
            Text(text = viewModel.templateFilePath.value.error ?: "", color = R.Color.errorText)
        }

        OutlinedTextField(
            value = viewModel.templateFilePathDestination.value.value,
            onValueChange = {
                viewModel.onTemplateFilePathDestinationChange(it)
            },
            label = { Text("Path Destination") },
            isError = (viewModel.templateFilePathDestination.value.error != null),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = R.Color.OutlinedTextField.focusedBorderColor,
                focusedLabelColor = R.Color.OutlinedTextField.focusedLabelColor
            )
        )
        if(viewModel.templateFilePathDestination.value.error != null) {
            Text(text = viewModel.templateFilePathDestination.value.error ?: "", color = R.Color.errorText)
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .weight(1f),
            value = viewModel.templateFileData.value,
            onValueChange = {
                viewModel.templateFileData.value = it
            }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = R.Color.ButtonDelete.background,
                    contentColor = R.Color.ButtonDelete.text
                ),
                onClick = {
                    viewModel.deleteTemplateFile(templateFile.id)
                }
            ) {
                Text(text = "Delete")
            }

            Button(
                modifier = Modifier,
                enabled = (viewModel.isSaveEnabled.value),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = R.Color.Button.background,
                    contentColor = R.Color.Button.text
                ),
                onClick = {
                    viewModel.saveTemplateFile(templateFile.id)
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}

