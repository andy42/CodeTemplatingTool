package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar
import com.jaehl.codeTool.ui.page.home.HomeViewModel
import com.jaehl.codeTool.ui.page.home.TemplateRow

@Composable
fun TemplateListPage(
    viewModel : TemplateListViewModel,
    onGoBackClicked: () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .background(R.Color.pageBackground)
        ) {
            AppBar(
                title = "TemplateList",
                returnButton = true,
                onBackClick = {
                    onGoBackClicked()
                }
            )
            Column {
                TemplateList(
                    viewModel = viewModel,
                    templates = viewModel.templates
                )
            }
        }
    }
}

@Composable
fun TemplateList(
    viewModel : TemplateListViewModel,
    templates : List<Template>
){
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(templates) { index, template ->
            TemplateRow(viewModel, index, template)
        }
    }
}

@Composable
fun TemplateRow(
    viewModel : TemplateListViewModel,
    index : Int,
    template : Template
){
    Text(
        text = template.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.onTemplateSelectClick(template)
            }
            //.background(if (index == selectedTemplateIndex) R.Color.rowBackgroundEven else R.Color.rowBackgroundOdd)
            .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
    )
}