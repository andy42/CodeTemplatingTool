package com.jaehl.codeTool.ui.page.templateList

import androidx.compose.runtime.mutableStateListOf
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.extensions.postSwap
import com.jaehl.codeTool.ui.navigation.NavBackListener
import com.jaehl.codeTool.ui.navigation.NavTemplateListener
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TemplateListViewModel @Inject constructor(
    private val logger : Logger,
    private val templateRepo : TemplateRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navTemplateListener : NavTemplateListener? = null
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

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    fun onTemplateSelectClick(template : Template) {
        navTemplateListener?.openTemplateEdit(template)
    }

    fun onTemplateAddClick() {
        navTemplateListener?.openTemplateEdit(null)
    }
}