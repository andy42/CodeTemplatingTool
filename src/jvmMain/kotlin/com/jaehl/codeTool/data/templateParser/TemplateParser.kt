package com.jaehl.codeTool.data.templateParser

import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateFile
import com.jaehl.codeTool.data.model.TemplateFileOutput
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import java.nio.file.Path

interface TemplateParser {
    fun parseString(value : String, variableValues : Map<String, String>) : String
    fun parse(template : Template, variableValues : Map<String, String>) : List<TemplateFileOutput>
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

    fun parseTemplateFile(templateFile: TemplateFile, variableValues : Map<String, String>) : TemplateFileOutput {
        logger.log(parseString(templateFile.path, variableValues))
        return TemplateFileOutput(
            path = parseString(templateFile.pathDestination, variableValues),
            data = parseString(
                fileUtil.loadFile(
                    Path.of(parseString(templateFile.path, variableValues))
                ),
                variableValues
            )
        )
    }
    override fun parse(template : Template, variableValues : Map<String, String>) : List<TemplateFileOutput> {
        return template.files.map {
            parseTemplateFile(it, variableValues)
        }
    }
}

