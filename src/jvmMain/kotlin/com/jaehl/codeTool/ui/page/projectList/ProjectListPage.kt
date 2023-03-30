package com.jaehl.codeTool.ui.page.projectList

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar

@Composable
fun ProjectListPage(
    viewModel : ProjectListViewModel,
    onGoBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(R.Color.pageBackground)
    ) {
        Column() {
            AppBar(
                title = "Projects",
                returnButton = false,
                onBackClick = {
                    onGoBackClicked()
                }
            )
            val state : ScrollState = rememberScrollState()

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
                        viewModel.onProjectAddClick()
                    },
                ) {
                    Text(text = "Add Project")
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    itemsIndexed(viewModel.projects) { index, project ->
                        ProjectRow(viewModel, index, project)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectRow(
    viewModel : ProjectListViewModel,
    index : Int,
    project : Project
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(R.Color.cardTitleBackground)
            .clickable {
                viewModel.onProjectSelectClick(project)
            }
    ){
        Text(
            text = project.name,
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                //.background(if (index == selectedTemplateIndex) R.Color.rowBackgroundEven else R.Color.rowBackgroundOdd)
                .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
        )
        IconButton(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd),
            content = {
                Icon(Icons.Outlined.Edit, "Edit", tint = R.Color.primary)
            }, onClick = {
                viewModel.onProjectEditClick(project)
            }
        )
    }
}