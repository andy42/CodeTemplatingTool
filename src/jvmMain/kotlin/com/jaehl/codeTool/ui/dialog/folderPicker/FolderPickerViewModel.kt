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
    private val onSelect : ((requestId : String, selectedFolder :  String) -> Unit)?,
    private val onSelectMulti : ((requestId : String, selectedFolders : List<String>) -> Unit)?,
    private val foldersOnly : Boolean
) : ViewModel() {

    private var folderPathList = ArrayList<String>()
    var folderPath = mutableStateListOf<String>()
        private set

    var folderItems = mutableStateListOf<FolderItem>()
        private set

    var selectedFolders = mutableStateListOf<String>()
        private set

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        viewModelScope.launch {
            if(startPath != null){
                folderPathList = ArrayList(startPath.split(File.separator))
                folderPath.postSwap(folderPathList)
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

    fun selectFolder(folderItem : FolderItem) = viewModelScope.launch {

        val newSelectedFolders = selectedFolders.toMutableList()

        if(onSelectMulti != null){
            if(newSelectedFolders.contains(folderItem.path)){
                newSelectedFolders.remove(folderItem.path)
            } else {
                newSelectedFolders.add(folderItem.path)
            }
        } else {
            newSelectedFolders.clear()
            newSelectedFolders.add(folderItem.path)
        }
        selectedFolders.postSwap(newSelectedFolders)
    }

    fun expandFolder(folderItem : FolderItem) = viewModelScope.launch {
        if(!folderItem.isFolder) return@launch
        folderPathList.add(folderItem.name)
        folderPath.postSwap(folderPathList)
        if(onSelectMulti == null){
            selectedFolders.clear()
        }

        updateFolderItems()
    }

    private suspend fun showRootDrives(){
        folderPathList.clear()
        folderPath.postSwap(folderPathList)
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
        folderPath.postSwap(folderPathList)
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