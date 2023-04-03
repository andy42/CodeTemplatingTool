package com.jaehl.codeTool.ui.util

import com.jaehl.codeTool.data.model.*
import java.io.File

interface OsPathConverter {
    fun getFileName(path : String) : String
    fun convertPath(path : String) : String
    fun convertPathsToOsFormat(project : Project) : Project
    fun convertPathsToOsFormat(template : Template) : Template
}

class OsPathConverterImp :  OsPathConverter{

    companion object {
        private const val localPathSeparator = "/"
    }

    override fun getFileName(path : String) : String {
        return path.split(localPathSeparator).lastOrNull() ?: ""
    }

    override fun convertPath(path : String) : String {
        return path.replace(localPathSeparator, File.separator)
    }

    private fun convertProjectVariable(projectVariable : ProjectVariable) : ProjectVariable {
        return when(projectVariable.type){
            TemplateVariableType.Path,  TemplateVariableType.Package -> ProjectVariable(
                name = projectVariable.name,
                type = projectVariable.type,
                value = convertPath(projectVariable.value)
            )
            else -> ProjectVariable(
                name = projectVariable.name,
                type = projectVariable.type,
                value = projectVariable.value
            )
        }
    }

    override fun convertPathsToOsFormat(project : Project) : Project {
        return Project(
            id = project.id,
            name = project.name,
            projectPath = convertPath(project.projectPath),
            kotlinSrcPath = convertPath(project.kotlinSrcPath),
            mainPackage = convertPath(project.mainPackage),
            variable = project.variable.map { convertProjectVariable(it) }
        )
    }

    private fun convertTemplateVariable(templateVariable : TemplateVariable) : TemplateVariable {
        return when(templateVariable.type){
            TemplateVariableType.Path,  TemplateVariableType.Package -> TemplateVariable(
                name = templateVariable.name,
                type = templateVariable.type,
            )
            else -> TemplateVariable(
                name = templateVariable.name,
                type = templateVariable.type,
            )
        }
    }

    private fun convertTemplateFile(templateFile : TemplateFile) : TemplateFile {
        return TemplateFile(
            path = convertPath(templateFile.path),
            pathDestination = convertPath(templateFile.pathDestination),
        )
    }

    override fun convertPathsToOsFormat(template : Template) : Template {
        return Template(
            id = template.id,
            name = template.name,
            dirPath = template.dirPath,
            variable = template.variable.map { convertTemplateVariable(it) },
            files = template.files.map { convertTemplateFile(it) }
        )
    }
}