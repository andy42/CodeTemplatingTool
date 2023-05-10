package com.jaehl.codeTool.ui.dialog.warningDialog

import com.jaehl.codeTool.ui.util.ViewModel
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class WarningViewModel @Inject constructor(
    private val logger : Logger
) : ViewModel() {

    private lateinit var config : WarningDialogConfig

    fun onAcceptClick() {
        config.acceptCallBack()
    }

    fun onDeclineClick() {
        config.declineCallBack()
    }

    fun init(viewModelScope: CoroutineScope, config : WarningDialogConfig) {
        super.init(viewModelScope)
        this.config = config
    }
}