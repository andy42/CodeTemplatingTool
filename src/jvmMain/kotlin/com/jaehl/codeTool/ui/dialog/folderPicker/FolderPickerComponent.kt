package com.jaehl.codeTool.ui.dialog.folderPicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger

class FolderPickerDialogComponent(
    private val componentContext: ComponentContext,
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val requestId : String,
    private val onDismissed : () -> Unit,
    private val startPath : String? = null,
    private val onSelect : ((requestId : String, selectedFolder : String) -> Unit)? = null,
    private val onSelectMulti : ((requestId : String, selectedFolder : List<String>) -> Unit)? = null,
    private val foldersOnly : Boolean = false
) : Component, ComponentContext by componentContext {

    private val viewModel = FolderPickerViewModel(
        logger,
        fileUtil,
        requestId,
        onDismissed,
        startPath = startPath,
        onSelect = onSelect,
        onSelectMulti = onSelectMulti,
        foldersOnly = foldersOnly
    )

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        FolderPickerPage(
            viewModel = viewModel
        )
    }
}