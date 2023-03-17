package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.AppBar

@Composable
fun TemplateEditPage(
    viewModel : TemplateEditViewModel,
    onGoBackClicked: () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .background(R.Color.pageBackground)
        ) {
            AppBar(
                title = "TemplateEdit",
                returnButton = true,
                onBackClick = {
                    onGoBackClicked()
                }
            )
            Row {
                Text("NEW Page")
            }
        }
    }
}