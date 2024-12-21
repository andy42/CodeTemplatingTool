package com.jaehl.codeTool.ui.dialog.folderPicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.codeTool.di.AppComponent
import com.jaehl.codeTool.ui.navigation.Component
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import javax.inject.Inject

data class FolderPickerDialogConfig(
    val requestId : String,
    val startPath : String? = null,
    val subPathOnly : Boolean = false,
    val foldersOnly : Boolean = false,
    val onDismissed : () -> Unit,
    val onSelect : ((requestId : String, selectedFolder :  String) -> Unit)? = null,
    val onSelectMulti : ((requestId : String, selectedFolders : List<String>) -> Unit)? = null,
)
class FolderPickerDialogComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val folderPickerDialogConfig : FolderPickerDialogConfig
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : FolderPickerViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, folderPickerDialogConfig)
        }

        FolderPickerPage(
            viewModel = viewModel
        )
    }
}