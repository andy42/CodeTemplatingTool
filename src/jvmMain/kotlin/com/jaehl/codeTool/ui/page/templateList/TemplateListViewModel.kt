package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.runtime.mutableStateListOf
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TemplateListViewModel(
    private val logger : Logger,
    private val templateRepo : TemplateRepo,
    private val onOpenTemplateEdit: (Template) -> Unit
) : ViewModel() {

    var templates = mutableStateListOf<Template>()
        private set

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        viewModelScope.launch {
            templateRepo.getTemplates().collect { templatesList ->
                templates.postSwap(templatesList)
            }
        }
    }

    fun onTemplateSelectClick(template : Template) {
        onOpenTemplateEdit(template)
    }
}