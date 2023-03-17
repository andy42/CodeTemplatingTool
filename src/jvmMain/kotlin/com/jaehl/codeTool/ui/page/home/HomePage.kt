package com.jaehl.codeTool.ui.page.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateFileOutput
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar
import com.jaehl.codeTool.ui.component.PackagePickerDialog
import com.jaehl.codeTool.ui.component.Picker

@Composable
fun HomePage(
    viewModel : HomeViewModel,
    onGoBackClicked: () -> Unit,
    onOpenTemplateList: () -> Unit
) {
    Box {
        Column(modifier = Modifier.background(R.Color.pageBackground)) {
            AppBar(
                title = "Home",
                returnButton = true,
                onBackClick = {
                    onGoBackClicked()
                }
            )
            Row {
                Column(
                    modifier = Modifier.width(170.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(start = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text(text = "Templates", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                        IconButton(
                            content = {
                                Icon(Icons.Outlined.Settings, "Settings", tint = R.Color.primary)
                            }, onClick = {
                                onOpenTemplateList()
                            }
                        )
                    }
                    TemplateList(
                        viewModel,
                        viewModel.templates,
                        viewModel.selectedTemplateIndex.value
                    )
                }
                MainPannel(viewModel)
            }
        }
        if (viewModel.isPackagePickerDialogOpen.value) {
            PackagePickerDialog(
                title = "Select Package",
                packageList = viewModel.packages,
                onPackageClick = { item ->
                    viewModel.onSelectedPackageClick(item)
                },
                onClose = {
                    viewModel.onClosePackagePickerDialog()
                }
            )
        }
    }
}

@Composable
fun MainPannel(
    viewModel : HomeViewModel
) {
    val state : ScrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 10.dp, end = 10.dp)
        .verticalScroll(state)
    ) {
        viewModel.variables.forEach{ variable ->
            when(variable) {
                is HomeViewModel.VariableString -> {
                    OutlinedTextField(
                        value = variable.value,
                        onValueChange = {viewModel.onVariableStringChange(variable.name, it)},
                        label = { Text(variable.name) },
                        modifier = Modifier.width(200.dp).padding(top = 5.dp)
                    )
                }
                is HomeViewModel.VariablePackage -> {
                    Picker(
                        title = variable.name,
                        value = variable.stringValue,
                        onClick = {
                            viewModel.onOpenPackagePickerDialog(variable.name)
                        },
                        modifier = Modifier.width(200.dp).padding(top = 5.dp)
                    )
                }
            }
        }
        TextButton(onClick = {
            viewModel.onSaveTemplateClick()
        }){
            Text(text = "Save Template")
        }

        viewModel.templateFileOutputs.forEach { templateFileOutput ->
            TemplateFile(templateFileOutput)
        }
    }
}

@Composable
fun TemplateFile(
    templateFileOutput: TemplateFileOutput
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                text = templateFileOutput.path,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(R.Color.cardTitleBackground)
                    .padding(top = 3.dp, bottom = 3.dp, start = 10.dp),
                color = R.Color.cardTitleText)

            Text(templateFileOutput.data, modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun TemplateList(
    viewModel : HomeViewModel,
    templates : List<Template>,
    selectedTemplateIndex : Int
){
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(templates) { index, template ->
            TemplateRow(viewModel, index, template, selectedTemplateIndex)
        }
    }
}

@Composable
fun TemplateRow(
    viewModel : HomeViewModel,
    index : Int,
    template : Template,
    selectedTemplateIndex : Int
){
    Text(
        text = template.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.onTemplateSelectClick(template, index)
            }
            .background(if (index == selectedTemplateIndex) R.Color.rowBackgroundEven else R.Color.rowBackgroundOdd)
            .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
    )
}