package com.jaehl.codeTool.ui.util

import com.jaehl.codeTool.data.model.*
import java.io.File
import javax.inject.Inject

interface OsPathConverter {
    fun getFileName(path : String) : String
    fun convertPath(path : String) : String
    fun convertPathsToOsFormat(project : Project) : Project
    fun convertPathsToOsFormat(template : Template) : Template
}

class OsPathConverterImp @Inject constructor(

):  OsPathConverter{

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
            TemplateVariableType.Path -> projectVariable.copy(
                value = convertPath(projectVariable.value)
            )
            else -> projectVariable.copy()
        }
    }

    override fun convertPathsToOsFormat(project : Project) : Project {
        return project.copy(
            projectPath = convertPath(project.projectPath),
            variable = project.variable.map { convertProjectVariable(it) }
        )
    }

    private fun convertTemplateVariable(templateVariable : TemplateVariable) : TemplateVariable {
        return when(templateVariable.type){
            TemplateVariableType.Path -> templateVariable.copy()
            else -> templateVariable.copy()
        }
    }

    private fun convertTemplateFile(templateFile : TemplateFile) : TemplateFile {
        return templateFile.copy(
            path = convertPath(templateFile.path),
            pathDestination = convertPath(templateFile.pathDestination),
        )
    }

    override fun convertPathsToOsFormat(template : Template) : Template {
        return template.copy(
            dirPath = convertPath(template.dirPath),
            variable = template.variable.map { convertTemplateVariable(it) },
            files = template.files.map { convertTemplateFile(it) }
        )
    }
}