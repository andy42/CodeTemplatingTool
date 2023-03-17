package com.jaehl.codeTool.ui.page.projectList

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
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
    Box {
        Column(
            modifier = Modifier
                .background(R.Color.pageBackground)
        ) {
            AppBar(
                title = "Projects",
                returnButton = false,
                onBackClick = {
                    onGoBackClicked()
                }
            )
            val state : ScrollState = rememberScrollState()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
//                    .fillMaxHeight()
//                    .verticalScroll(state)
            ) {
                itemsIndexed(viewModel.projects) { index, project ->
                    ProjectRow(viewModel, index, project)
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
    Text(
        text = project.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.onProjectSelectClick(project)
            }
            //.background(if (index == selectedTemplateIndex) R.Color.rowBackgroundEven else R.Color.rowBackgroundOdd)
            .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
    )
}