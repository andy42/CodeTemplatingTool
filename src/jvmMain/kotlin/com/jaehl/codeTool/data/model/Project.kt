package com.jaehl.codeTool.data.model

import com.jaehl.codeTool.ui.util.OsPathConverter

data class Project(
    val id : String = "",
    val name : String = "",
    val projectPath : String = "",
    val variable : List<ProjectVariable> = listOf()
)

data class ProjectVariable(
    val name : String = "",
    val type : TemplateVariableType = TemplateVariableType.String,
    val value : String = ""
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
                    type = TemplateVariableType.Path,
                    value = osPathConverter.convertPath("/com/example/projectName")
                )
            )
        }
    }
}
