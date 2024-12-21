package com.jaehl.codeTool.ui.dialog.TemplateCloneDialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.ui.component.TextFieldValue
import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TemplateCloneViewModel @Inject constructor(
    private val logger : Logger,
    private val templateRepo : TemplateRepo,
    private val fileUtil : FileUtil
) : ViewModel() {

    private var templateId : String? = null

    var uiEvent by mutableStateOf<UiEvent>(UiEvent.Empty)
        private set

    var newTemplateName by mutableStateOf (TextFieldValue())
        private set

    fun onNewTemplateNameChange(value : String){
        newTemplateName = TextFieldValue(value = value)
    }

    fun onCloneClick(){
        viewModelScope.launch {
            val template = templateRepo.getTemplate(templateId) ?: return@launch
            if(newTemplateName.value.isEmpty()){
                newTemplateName = newTemplateName.copy(error = "name is empty")
                return@launch
            }
            templateRepo.cloneTemplate(template, newTemplateName.value)?.let { newTemplate ->
                uiEvent = UiEvent.TemplateCloned(newTemplate)
            }
        }
    }

    fun clearUiEvent(){
        uiEvent = UiEvent.Empty
    }

    fun init(viewModelScope: CoroutineScope, templateId : String) {
        super.init(viewModelScope)
        this.templateId = templateId
    }

    sealed class UiEvent {
        object Empty : UiEvent()
        data class TemplateCloned(val template : Template) : UiEvent()
    }
}