package com.jaehl.codeTool.ui.dialog.warningDialog

data class WarningDialogConfig(
    val title : String = "Warning",
    val message : String,
    val acceptCallBack : () -> Unit = {},
    val declineCallBack : () -> Unit = {},
    val acceptText : String = "Ok",
    val declineText : String? = null
)
