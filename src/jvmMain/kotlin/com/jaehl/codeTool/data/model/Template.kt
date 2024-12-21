package com.jaehl.codeTool.data.model

data class TemplateFile(
    val path : String = "",
    val pathDestination : String = ""
)

enum class TemplateVariableType(val value : kotlin.String){
    String("String"),
    Path("Path");

    override fun toString(): kotlin.String {
        return value
    }
}

data class TemplateVariable(
    val name : String = "",
    val type : TemplateVariableType = TemplateVariableType.String,
    val projectVariable : Boolean = false,
    val startPath : String = "",
    val default : String = ""
)

data class TemplateFileOutput(
    val path : String = "",
    val data : String = ""
)

data class Template(
    val id : String = "",
    val name : String = "",
    val dirPath : String = "",
    val variable : List<TemplateVariable> = arrayListOf(),
    val files : List<TemplateFile> = arrayListOf()
)
