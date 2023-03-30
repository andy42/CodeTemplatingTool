package com.jaehl.codeTool.data.model

import com.jaehl.codeTool.ui.util.OsPathConverter

data class Project(
    var id : String = "",
    var name : String = "",
    var projectPath : String = "",
    var kotlinSrcPath : String = "",
    var mainPackage : String = "",
    var variable : List<ProjectVariable> = listOf()
)

data class ProjectVariable(
    var name : String = "",
    var type : TemplateVariableType,
    var value : String
) {
    companion object {
        fun createDefaults(osPathConverter : OsPathConverter) : List<ProjectVariable>{
            return listOf(
                ProjectVariable(
                    name = "srcPath",
                    type = TemplateVariableType.Path,
                    value = osPathConverter.convertPath("/src/jvmMain/kotlin")
                ),
                ProjectVariable(
                    name = "testPath",
                    type = TemplateVariableType.Path,
                    value = osPathConverter.convertPath("/src/jvmTest/kotlin")
                ),
                ProjectVariable(
                    name = "mainPackage",
                    type = TemplateVariableType.Package,
                    value = osPathConverter.convertPath("/com/example/projectName")
                )
            )
        }
    }
}
