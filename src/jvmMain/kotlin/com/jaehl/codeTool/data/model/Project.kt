package com.jaehl.codeTool.data.model

data class Project(
    var id : String = "",
    var name : String = "",
    var projectPath : String = "",
    var kotlinSrcPath : String = "",
    var mainPackage : String = ""
){
    companion object {
        fun temp() = Project(
            id = "0",
            name = "Code Tool",
            projectPath = "C:\\Users\\andy\\IdeaProjects\\CodeTool",
            kotlinSrcPath = "\\src\\jvmMain\\kotlin",
            mainPackage = "\\com\\jaehl\\codeTool"
        )
    }
}
