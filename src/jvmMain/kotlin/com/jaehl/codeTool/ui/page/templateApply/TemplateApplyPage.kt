package com.jaehl.codeTool.ui.page.templateApply

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.TemplateFileOutput
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.*

@Composable
fun HomePage(
    viewModel : TemplateApplyViewModel,
    onGoBackClicked: () -> Unit,
    onOpenTemplateList: () -> Unit
) {
    Box {
        Column(modifier = Modifier.background(R.Color.pageBackground)) {
            AppBar(
                title = "Apply Template",
                returnButton = true,
                onBackClick = {
                    onGoBackClicked()
                }
            )
            Row {
                NavView(viewModel, onOpenTemplateList)
                VerticalDivider(
                    thickness = 1.dp,
                    color = R.Color.dividerColor
                )
                MainPannel(viewModel)
            }
        }
    }
}

@Composable
fun NavView(
    viewModel : TemplateApplyViewModel,
    onOpenTemplateList: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(250.dp)
    ) {
        NavRowHeading(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp),
            title = "Templates",
            icon = Icons.Outlined.Edit,
            iconDescription = "Edit Templates",
            onIconClick = {
                onOpenTemplateList()
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(viewModel.templates) { index, template ->
                NavRowTitle(
                    modifier = Modifier
                        .fillMaxWidth(),
                    iconBitmap = null,
                    title = template.name,
                    selected = (viewModel.selectedTemplateIndex.value == index),
                    onClick = {
                        viewModel.onTemplateSelectClick(template, index)
                    }
                )
            }
        }
    }
}

@Composable
fun MainPannel(
    viewModel : TemplateApplyViewModel
) {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(10.dp)
        .fillMaxWidth()
        .background(R.Color.cardBackground)
        .padding(10.dp)
    ) {
        viewModel.variables.forEachIndexed{ index, variable ->
            when(variable) {
                is TemplateApplyViewModel.VariableString -> {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        value = variable.value,
                        onValueChange = {viewModel.onVariableStringChange(index, it)},
                        label = { Text(variable.name) },
                    )
                }
                is TemplateApplyViewModel.VariablePath -> {
                    Picker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        title = variable.name,
                        value = variable.value,
                        onClick = {
                            viewModel.onOpenPathPickerDialog(index, variable.name)
                        }

                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp)
        ) {
            Button(
                modifier = Modifier,
                onClick = {
                    viewModel.onSaveTemplateClick()
                }
            ) {
                Text(text = "Apply Template")
            }
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
                    .background(R.Color.primary)
                    .padding(top = 5.dp, bottom = 5.dp, start = 10.dp),
                color = R.Color.textLight)

            Text(
                text = templateFileOutput.data,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(R.Color.codeBlock)
                    .padding(10.dp)
            )
        }
    }
}