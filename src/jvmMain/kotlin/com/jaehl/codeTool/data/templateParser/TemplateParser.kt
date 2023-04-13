package com.jaehl.codeTool.data.templateParser

import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateFile
import com.jaehl.codeTool.data.model.TemplateFileOutput
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import java.nio.file.Path

interface TemplateParser {
    fun parseString(value : String, variableValues : Map<String, String>) : String
    fun parse(project : Project, template : Template, variableValues : Map<String, String>) : List<TemplateFileOutput>
}

class TemplateParserImp(
    private val fileUtil : FileUtil,
    private val logger: Logger
) : TemplateParser{

    override fun parseString(value : String, variableValues : Map<String, String>) : String{
        var newValue = value
        variableValues.forEach{
            newValue = newValue.replace("{{${it.key}}}", it.value)
        }
        return newValue
    }

    private fun parseTemplateFile(
        templateDir : String,
        templateFile: TemplateFile,
        projectDir: String,
        variableValues : Map<String, String>) : TemplateFileOutput {

        return TemplateFileOutput(
            path = parseString(projectDir+templateFile.pathDestination, variableValues),
            data = parseString(
                fileUtil.loadFile(
                    Path.of(parseString(templateDir+templateFile.path, variableValues))
                ),
                variableValues
            )
        )
    }
    override fun parse(project : Project, template : Template, variableValues : Map<String, String>) : List<TemplateFileOutput> {
        val templateDir = System.getProperty("user.home") +pathSeparator+ "CodeTool"+ pathSeparator+ template.dirPath
        return template.files.map {
            parseTemplateFile(
                templateDir= templateDir,
                templateFile= it,
                projectDir= project.projectPath,
                variableValues= variableValues)
        }
    }

    private val pathSeparator = fileUtil.getPathSeparator()
}

