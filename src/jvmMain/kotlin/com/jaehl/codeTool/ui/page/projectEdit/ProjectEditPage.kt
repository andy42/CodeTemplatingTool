package com.jaehl.codeTool.ui.page.projectEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.ProjectVariable
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar
import com.jaehl.codeTool.ui.component.Picker

@Composable
fun ProjectEditPage(
    viewModel : ProjectEditViewModel
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(R.Color.pageBackground)
    ) {
        AppBar(
            title = "ProjectEdit",
            returnButton = true,
            onBackClick = {
                viewModel.onNavBackClick()
            }
        )

        Card(
            modifier = Modifier
                .width(800.dp)
                .background(R.Color.pageBackground)
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                .align(Alignment.CenterHorizontally),
        ) {

            Column(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = viewModel.projectName.value,
                    onValueChange = { viewModel.onProjectNameChange(it) },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                )

                Picker(
                    title = "Project Path",
                    value = viewModel.projectPath.value,
                    onClick = {
                        viewModel.onOpenPackagePickerDialog()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
                )

                viewModel.projectVariables.forEachIndexed { index, projectVariable ->
                    ProjectVariable(
                        viewModel,
                        index,
                        projectVariable
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
                            backgroundColor = R.Color.deleteButtonBackground,
                            contentColor = R.Color.deleteButtonText
                        ),
                        onClick = {
                            viewModel.delete()
                        }
                    ) {
                        Text(text = "Delete")
                    }
                    Button(
                        modifier = Modifier,
                        onClick = {
                            viewModel.addVariable()
                        }
                    ) {
                        Text(text = "Add Variable")
                    }

                    Button(
                        modifier = Modifier,
                        onClick = {
                            viewModel.openDefaultVariablePickerDialog()
                        }
                    ) {
                        Text(text = "Add Default Variable")
                    }

                    Button(
                        modifier = Modifier,
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
    }
}

@Composable
fun ProjectVariable(
    viewModel : ProjectEditViewModel,
    index : Int,
    projectVariable : ProjectVariable,
) {
    Column(
        modifier = Modifier
            .padding(top = 40.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(R.Color.cardSubTitleBackground)
        ) {
            Text(
                text = if(projectVariable.name.isEmpty()) "Variable ${index +1}" else projectVariable.name,
                color = R.Color.textLight,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp)
            )
            IconButton(
                modifier = Modifier,
                content = {
                    Icon(Icons.Outlined.Delete, "Delete", tint = R.Color.textLight)
                }, onClick = {
                    viewModel.onProjectVariableDelete(index)
                }
            )
        }
        OutlinedTextField(
            value = projectVariable.name,
            onValueChange = {
                viewModel.onProjectVariableNameChange(index, it)
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )

        Picker(
            title = "Type",
            value = projectVariable.type.value,
            onClick = {
                viewModel.onProjectVariableTypeClick(index)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )

        OutlinedTextField(
            value = projectVariable.value,
            onValueChange = {
                viewModel.onProjectVariableValueChange(index, it)
            },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )
    }

}