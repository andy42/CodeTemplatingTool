package com.jaehl.codeTool.ui.page.templateEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateFile
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class TemplateEditViewModel(
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
    private val template : Template?
) : ViewModel() {

    var name = mutableStateOf<String>(template?.name ?: "")
    var files = mutableStateListOf<TemplateFileViewModel>()
        private set

    var selectedNavRow = mutableStateOf<NavRowSelect>(NavRowSelect.NavRowGeneralInfoSelect)

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        viewModelScope.launch {
            files.postSwap(template?.files?.map { TemplateFileViewModel.create(it) } ?: listOf())
        }
    }

    fun onNameChange(name : String) = viewModelScope.launch {
        this@TemplateEditViewModel.name.value = name
    }

    fun onGeneralInfoClick() {
        selectedNavRow.value = NavRowSelect.NavRowGeneralInfoSelect
    }

    fun onTemplateClick(index : Int ) {
        selectedNavRow.value = NavRowSelect.NavRowFileSelect(index)
    }

    sealed class NavRowSelect {
        object NavRowGeneralInfoSelect : NavRowSelect()
        class NavRowFileSelect(val index : Int) : NavRowSelect()
    }
}

data class TemplateFileViewModel(
    val name : String = "",
    val path : String = "",
    val savedPath : String? = null,
    val pathDestination : String = "",
    val fileData : String = ""
) {
    companion object {
        fun create(templateFile : TemplateFile) : TemplateFileViewModel {
            return TemplateFileViewModel(
                name = templateFile.path.split(File.separator).lastOrNull() ?: "",
                path = templateFile.path,
                savedPath = templateFile.path,
                pathDestination = templateFile.pathDestination
            )
        }
    }
}