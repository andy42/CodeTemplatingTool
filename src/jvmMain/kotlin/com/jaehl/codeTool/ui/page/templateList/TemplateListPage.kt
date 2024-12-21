package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar
import com.jaehl.codeTool.ui.component.Icons

@Composable
fun TemplateListPage(
    viewModel : TemplateListViewModel,
    onCloneTemplateClick : (Template) -> Unit
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
                    TemplateRow(viewModel, index, template, onCloneTemplateClick)
                }
            }
        }
    }
}

@Composable
fun TemplateRow(
    viewModel : TemplateListViewModel,
    index : Int,
    template : Template,
    onCloneTemplateClick : (Template) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(R.Color.cardTitleBackground)

    ){
        Text(
            text = template.name,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .clickable {
                    viewModel.onTemplateSelectClick(template)
                }
                .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)

        )

        Box(modifier = Modifier
            .align(Alignment.CenterVertically)
            .fillMaxHeight()
            .clickable {
                onCloneTemplateClick(template)
            },
            contentAlignment = Alignment.Center
        ){
            Icon(
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp),
                imageVector = Icons.CopyAll,
                contentDescription = "Not Supported",
                tint = R.Color.rowText
            )
        }
    }
}