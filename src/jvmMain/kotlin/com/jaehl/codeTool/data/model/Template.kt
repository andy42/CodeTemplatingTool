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
    var name : String = "",
    var type : TemplateVariableType = TemplateVariableType.String,
    var projectVariable : Boolean = false,
    var startPath : String = "",
    var default : String = ""
)

data class TemplateFileOutput(
    val path : String = "",
    val data : String = ""
)

data class Template(
    var id : String = "",
    var name : String = "",
    var dirPath : String = "",
    var variable : List<TemplateVariable> = arrayListOf(),
    var files : List<TemplateFile> = arrayListOf()
)
