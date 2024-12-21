package com.jaehl.codeTool.ui.component

data class TextFieldValue(
    val value : String = "",
    val error : String = ""
) {
    fun hasError() = error.isNotEmpty()
    fun copySetValue(value : String) = copy(value = value)
    fun copySetError(error : String) = copy(error = error)
}