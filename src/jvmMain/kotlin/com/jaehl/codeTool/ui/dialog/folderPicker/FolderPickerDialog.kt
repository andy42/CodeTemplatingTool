package com.jaehl.codeTool.ui.dialog.folderPicker

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import com.jaehl.codeTool.ui.R
import com.jaehl.codeTool.ui.component.DialogTitleBar

@Composable
fun FolderPickerPage(
    viewModel : FolderPickerViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(R.Color.dialogBackground)
    ) {
        Column(
            modifier = Modifier
                .width(800.dp)
                .height(900.dp)
                .background(R.Color.pageBackground)
                .align(alignment = Alignment.Center)
        ) {
            DialogTitleBar(
                title = "FolderPicker",
                onClose = {
                    viewModel.onCloseClick()
                }
            )
            Row(
                modifier = Modifier.padding(5.dp)
            ) {
                if(!viewModel.subPathOnly) {
                    FolderBreadcrumbChip(
                        viewModel = viewModel,
                        name = "...",
                        pathIndex = -1,
                        selectable = true
                    )
                }
                viewModel.folderBreadcrumb.forEach{ item ->
                    FolderBreadcrumbChip(
                        viewModel = viewModel,
                        name = item.name,
                        pathIndex = item.pathIndex,
                        selectable = item.selectable
                    )
                }
            }
            FolderItemList(
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                folderItems = viewModel.folderItems,
                selectedFolders = viewModel.selectedFolders
            )
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .padding(top = 5.dp, bottom = 5.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = R.Color.Button.background,
                        contentColor = R.Color.Button.text
                    ),
                    enabled = (viewModel.selectedFolders.isNotEmpty()),
                    onClick = {
                        viewModel.submit()
                    },
                ) {
                    Text(text = "Select")
                }
            }
        }
    }
}

@Composable
fun FolderBreadcrumbChip(
    viewModel : FolderPickerViewModel,
    name : String,
    pathIndex : Int,
    selectable : Boolean
){
    Box(
        modifier = Modifier
            .clickable {
                if(selectable) {
                    viewModel.selectPreviewsFolder(pathIndex)
                }
            }
            .padding(start = 5.dp)
            .background(if(selectable) R.Color.Tertiary.background else R.Color.Disabled.background)
    ) {
        Text(
            text = name,
            color = if(selectable) R.Color.Tertiary.content else R.Color.Disabled.content,
            modifier = Modifier
                .padding(5.dp)
        )
    }
}

@Composable
fun FolderItemList(
    modifier : Modifier,
    viewModel : FolderPickerViewModel,
    folderItems : List<FolderItem>,
    selectedFolders : List<String>
){
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(folderItems) { index, folderItem ->
            FolderItemRow(viewModel, index, folderItem, selectedFolders)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderItemRow(
    viewModel : FolderPickerViewModel,
    index : Int,
    folderItem : FolderItem,
    selectedFolders : List<String>
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                enabled = true,
                onDoubleClick = {
                    viewModel.expandFolder(folderItem)
                },
                onClick = {
                    viewModel.selectFolder(folderItem)
                }
            )
            .background(if (selectedFolders.contains(folderItem.path)) R.Color.rowSelectedBackground else R.Color.rowBackground)
            .padding(start = 10.dp, top = 3.dp, bottom = 3.dp)
    ) {
        val folder = remember { useResource("folder.png") { loadImageBitmap(it) }}
        val file = remember { useResource("file.png") { loadImageBitmap(it) }}

        Image(
            bitmap = if (folderItem.isFolder) folder else file ,
            if (folderItem.isFolder) "folder" else "file",
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
        )

        Text(
            text = folderItem.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 3.dp, bottom = 3.dp),
            color = if (selectedFolders.contains(folderItem.path)) R.Color.rowSelectedText else R.Color.rowText
        )
    }
}