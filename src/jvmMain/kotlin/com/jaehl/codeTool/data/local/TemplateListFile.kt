package com.jaehl.codeTool.data.local

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.util.Logger
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

interface TemplateListFile {
    fun load()  : List<Template>
    fun save(templates : List<Template>) : Boolean
}

class TemplateListFileImp(
    private val logger: Logger
) : TemplateListFile {

    private fun getFile() : File {
        val directory = Paths.get(System.getProperty("user.home"), projectUserDir)
        if( !directory.exists()){
            directory.createDirectory()
        }
        return Paths.get(System.getProperty("user.home"), projectUserDir, templateListFile).toFile()
    }

    override fun load(): List<Template> {
        val file = getFile()

        if(!file.exists()) {
            println("ERROR : TemplateListFileImp does not exist\n${file.absoluteFile} ")
            return listOf()
        }
        val gson = Gson().newBuilder().create()
        val fileString = file.inputStream().readBytes().toString(Charsets.UTF_8)
        return gson.fromJson<List<Template>>(fileString, object : TypeToken<List<Template>>() {}.type)
    }

    override fun save(templates: List<Template>): Boolean {
        return try {
            val file = getFile()
            println("file : ${file.absoluteFile}")
            file.createNewFile()

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(templates)
            file.writeText(jsonString, Charsets.UTF_8)
            true
        } catch (t : Throwable){
            logger.error("TemplateListFileImp "+t.message)
            false
        }
    }

    companion object {
        private val projectUserDir = "CodeTool"
        private val templateListFile = "templateList.json"
    }
}