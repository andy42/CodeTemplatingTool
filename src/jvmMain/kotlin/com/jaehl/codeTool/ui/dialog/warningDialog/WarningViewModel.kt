package com.jaehl.codeTool.ui.dialog.warningDialog

import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope

class WarningViewModel(
    private val logger : Logger,
    private val decline: () -> Unit,
    private val onAccept: () -> Unit
) : ViewModel() {

    fun onAcceptClick() {
        onAccept()
    }

    fun onDeclineClick() {
        decline()
    }

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)
    }
}