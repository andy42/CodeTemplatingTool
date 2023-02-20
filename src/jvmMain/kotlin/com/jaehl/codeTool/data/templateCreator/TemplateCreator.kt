package com.jaehl.codeTool.data.templateCreator

import androidx.compose.ui.graphics.Path
import com.jaehl.codeTool.data.model.TemplateFileOutput
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import java.nio.file.Files
import java.nio.file.Paths

interface TemplateCreator {
    fun createFile(templateFileOutput : TemplateFileOutput) : Boolean
    fun createFile(list : List<TemplateFileOutput>) : Boolean
}

class TemplateCreatorImp(
    private val fileUtil : FileUtil,
    private val logger: Logger
) : TemplateCreator {

    override fun createFile(templateFileOutput : TemplateFileOutput) : Boolean {
        val path = Paths.get(templateFileOutput.path)

        Files.createDirectories(path.parent)
        return fileUtil.writeFile(path, templateFileOutput.data)
    }

    override fun createFile(list: List<TemplateFileOutput>): Boolean {
        list.forEach { templateFileOutput ->
            if(!createFile(templateFileOutput)) return false
        }
        return true
    }
}