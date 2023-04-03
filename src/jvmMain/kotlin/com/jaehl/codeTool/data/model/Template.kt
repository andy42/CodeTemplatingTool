package com.jaehl.codeTool.data.model

data class TemplateFile(
    var path : String = "",
    var pathDestination : String = ""
)

enum class TemplateVariableType(val value : kotlin.String){
    String("String"),
    Path("Path"),
    Package("Package");

    override fun toString(): kotlin.String {
        return value
    }
}

data class TemplateVariable(
    val name : String = "",
    val type : TemplateVariableType = TemplateVariableType.String,
    val projectVariable : Boolean = false,
    val default : String = ""
)

data class TemplateFileOutput(
    val path : String = "",
    val data : String = ""
)

data class Template(
    var id : String = "",
    val name : String = "",
    val dirPath : String = "",
    val variable : List<TemplateVariable> = arrayListOf(),
    val files : List<TemplateFile> = arrayListOf()
)
