package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar

@Composable
fun TemplateListPage(
    viewModel : TemplateListViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(R.Color.pageBackground)
    ) {
        AppBar(
            title = "Templates",
            returnButton = true,
            onBackClick = {
                viewModel.onBackClick()
            }
        )

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .width(400.dp)
        ) {
            Button(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(top = 10.dp),
                onClick = {
                    viewModel.onTemplateAddClick()
                },
            ) {
                Text(text = "Add Template")
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                itemsIndexed(viewModel.templates) { index, template ->
                    TemplateRow(viewModel, index, template)
                }
            }
        }
    }
}

@Composable
fun TemplateRow(
    viewModel : TemplateListViewModel,
    index : Int,
    template : Template
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(R.Color.cardTitleBackground)
            .clickable {
                viewModel.onTemplateSelectClick(template)
            }
    ){
        Text(
            text = template.name,
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
        )
    }
}