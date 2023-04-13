package com.jaehl.codeTool.ui.dialog.folderPicker

import androidx.compose.runtime.mutableStateListOf
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.*

class FolderPickerViewModel(
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val requestId : String,
    private val onDismissed : () -> Unit,
    private val startPath : String?,
    var subPathOnly : Boolean,
    private val onSelect : ((requestId : String, selectedFolder :  String) -> Unit)?,
    private val onSelectMulti : ((requestId : String, selectedFolders : List<String>) -> Unit)?,
    private val foldersOnly : Boolean
) : ViewModel() {

    //the currently selected folder path
    private var folderPathList = ArrayList<String>()

    var folderBreadcrumb = mutableStateListOf<FolderBreadcrumbItem>()
        private set

    var folderItems = mutableStateListOf<FolderItem>()
        private set

    var selectedFolders = mutableStateListOf<String>()
        private set

    var subPathStartIndex = 0

    private suspend fun updateFolderBreadcrumb() {
        var newBreadcrumb = folderPathList.mapIndexed{ index, name ->
            FolderBreadcrumbItem(
                pathIndex = index,
                name = name,
                selectable = if(subPathOnly) (index >= subPathStartIndex )else true
            )
        }.takeLast(4)

        folderBreadcrumb.postSwap(
            newBreadcrumb
        )
    }
    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        viewModelScope.launch {
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
        onDismissed()
    }

    private fun buildFolderPath() : String {
        return folderPathList.joinToString(File.separator)
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
            .filter{ if(foldersOnly) it.isFolder else true  }

        folderItems.postSwap(items)
    }

    fun selectFolder(folderItem : FolderItem) {

        if(onSelectMulti != null){
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
        if(onSelectMulti == null){
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
        onDismissed()
        onSelect?.invoke(requestId, selectedFolders.toList().first())
        onSelectMulti?.invoke(requestId, selectedFolders.toList())
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