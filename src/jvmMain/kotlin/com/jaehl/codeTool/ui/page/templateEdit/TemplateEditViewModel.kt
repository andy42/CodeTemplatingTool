package com.jaehl.codeTool.ui.page.templateEdit

import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TemplateEditViewModel(
    private val logger : Logger,
    private val fileUtil : FileUtil,
    private val templateRepo : TemplateRepo,
) : ViewModel() {

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)
    }
}