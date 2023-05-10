package com.jaehl.codeTool.ui.dialog.folderPicker

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.io.path.*

class FolderPickerViewModel @Inject constructor(
    private val logger : Logger,
    private val fileUtil : FileUtil
) : ViewModel() {

    private lateinit var config : FolderPickerDialogConfig
    //the currently selected folder path
    private var folderPathList = ArrayList<String>()

    var folderBreadcrumb = mutableStateListOf<FolderBreadcrumbItem>()
        private set

    var folderItems = mutableStateListOf<FolderItem>()
        private set

    var selectedFolders = mutableStateListOf<String>()
        private set

    var subPathStartIndex = 0

    val subPathOnly = mutableStateOf(false)

    private suspend fun updateFolderBreadcrumb() {
        var newBreadcrumb = folderPathList.mapIndexed{ index, name ->
            FolderBreadcrumbItem(
                pathIndex = index,
                name = name,
                selectable = if(config.subPathOnly) (index >= subPathStartIndex )else true
            )
        }.takeLast(4)

        folderBreadcrumb.postSwap(
            newBreadcrumb
        )
    }
    fun init(viewModelScope: CoroutineScope, folderPickerDialogConfig: FolderPickerDialogConfig) {
        super.init(viewModelScope)

        this.config = folderPickerDialogConfig

        viewModelScope.launch {
            subPathOnly.value = config.subPathOnly
            val startPath = config.startPath
            if(startPath != null){
                folderPathList = ArrayList(startPath.split(File.separator))
                subPathStartIndex = folderPathList.size -1
                updateFolderBreadcrumb()
                selectedFolders.clear()
                updateFolderItems()
            } else {
                showRootDrives()
            }
        }
    }

    fun onCloseClick() = viewModelScope.launch {
        config.onDismissed()
    }

    private fun buildFolderPath() : String {
        return if(folderPathList.size == 1){
            folderPathList.first() + File.separator
        } else {
            folderPathList.joinToString(File.separator)
        }
    }

    private suspend fun updateFolderItems() {
        val path = buildFolderPath()
        val items = Path(path).listDirectoryEntries()
            .map {
                FolderItem(
                    it.name,
                    path+File.separator+it.name,
                    it.isDirectory()
                )
            }
            .sortedByDescending { it.isFolder }
            .filter{ if(config.foldersOnly) it.isFolder else true  }

        folderItems.postSwap(items)
    }

    fun selectFolder(folderItem : FolderItem) {

        if(config.onSelectMulti != null){
            if(selectedFolders.contains(folderItem.path)){
                selectedFolders.remove(folderItem.path)
            } else {
                selectedFolders.add(folderItem.path)
            }
        } else {
            selectedFolders.clear()
            selectedFolders.add(folderItem.path)
        }
    }

    fun expandFolder(folderItem : FolderItem) = viewModelScope.launch {
        if(!folderItem.isFolder) return@launch
        folderPathList.add(folderItem.name)
        updateFolderBreadcrumb()
        if(config.onSelectMulti == null){
            selectedFolders.clear()
        }

        updateFolderItems()
    }

    private suspend fun showRootDrives(){
        folderPathList.clear()
        updateFolderBreadcrumb()
        folderItems.postSwap(
            fileUtil.getRootDirectories().map { FolderItem(name = it, path = it, isFolder = true) }
        )
        selectedFolders.clear()
    }

    fun selectPreviewsFolder(index : Int) = viewModelScope.launch {
        if(index < 0) {
            showRootDrives()
            return@launch
        }

        folderPathList = ArrayList(folderPathList.subList(0, index+1))
        updateFolderBreadcrumb()
        selectedFolders.clear()
        updateFolderItems()
    }

    fun submit() {
        config.onDismissed()
        config.onSelect?.invoke(config.requestId, selectedFolders.toList().first())
        config.onSelectMulti?.invoke(config.requestId, selectedFolders.toList())
    }
}

data class FolderItem(
    val name : String,
    val path : String,
    val isFolder : Boolean
)

data class FolderBreadcrumbItem(
    val pathIndex : Int,
    val name : String,
    val selectable : Boolean
)