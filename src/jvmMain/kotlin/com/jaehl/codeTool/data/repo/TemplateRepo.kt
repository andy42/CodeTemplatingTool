package com.jaehl.codeTool.data.repo

import com.jaehl.codeTool.data.local.ObjectListLoader
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.model.TemplateFile
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.util.FileUtil
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.nio.file.Path
import kotlin.io.path.exists

class TemplateRepo(
    private val logger: Logger,
    private val templateListLoader : ObjectListLoader<Template>,
    private val osPathConverter : OsPathConverter,
    private val fileUtil : FileUtil
) {

    private val templateMap = LinkedHashMap<String, Template>()
    private var loaded = false

    private var templates = MutableSharedFlow<List<Template>>(replay = 1)

    init {
        GlobalScope.async {
            loadLocal(true)
            templates.tryEmit(templateMap.values.toList())
        }
    }

    fun getTemplates() : SharedFlow<List<Template>> = templates

    suspend fun getTemplate(id : String?) : Template? {
        if (id == null) return null
        return templateMap[id]
    }

    private fun createNewTemplateId() : String {
        var index = 0
        while (true){
            if(!templateMap.containsKey(index.toString())){
                return index.toString()
            }
            index++
        }
    }

    private fun createTemplateDir(template : Template) : String{
        return fileUtil.getPathSeparator()+template.name
    }

    fun updateTemplate(oldTemplate : Template?, newTemplate : Template) : Template{
        var template = newTemplate.copy(dirPath = createTemplateDir(newTemplate))
        if(template.id.isEmpty()){
            template.id = createNewTemplateId()
        }
        if(oldTemplate?.dirPath.isNullOrBlank()){

            fileUtil.createDirectory(Path.of(templateUserDir+template.dirPath))
        }
        else {
            oldTemplate?.dirPath?.let {
                if(!fileUtil.renameDirectory(Path.of(templateUserDir+it),Path.of(templateUserDir+template.dirPath) )){
                    logger.error("TemplateRepo::updateTemplate error" )
                }
            }
        }

        templateMap[template.id] = template
        templateListLoader.save(templateMap.values.toList())
        templates.tryEmit(templateMap.values.toList())
        return template
    }

    fun deleteTemplate(template : Template){
        if(template.id.isEmpty()){
            template.id = createNewTemplateId()
        }
        for(templateFile in template.files) {
            fileUtil.deleteFile(Path.of(templateUserDir+template.dirPath+templateFile.path))
        }

        templateMap.remove(template.id)
        templateListLoader.save(templateMap.values.toList())
        templates.tryEmit(templateMap.values.toList())
    }

    fun templateNameExist(name : String) : Boolean {
        templateMap.values.forEach{
            if(it.name == name) return true
        }
        return false
    }

    fun templateDirPathExist(dir : String) : Boolean {
        return Path.of(templateUserDir+dir).exists()
    }

    fun templateFilePathExist(template : Template, templateFilePath : String) : Boolean {
        return Path.of(templateUserDir+template.dirPath+templateFilePath).exists()
    }

    private fun getTemplateFilePath(template : Template, templateFile : TemplateFile) : Path {
        return Path.of(templateUserDir+template.dirPath+templateFile.path)
    }

    private fun moveTemplateFile(template : Template, currentTemplateFile : TemplateFile, newTemplateFile : TemplateFile) {
        fileUtil.moveFile(
            src = getTemplateFilePath(template, currentTemplateFile),
            dest = getTemplateFilePath(template, newTemplateFile))
    }

    fun loadTemplateFile(template : Template, templateFile : TemplateFile) : String{
        return fileUtil.loadFile(Path.of(templateUserDir+template.dirPath+templateFile.path))
    }

    fun writeTemplateFile(template : Template, templateFile : TemplateFile, fileData : String){
        fileUtil.writeFile(getTemplateFilePath(template, templateFile), fileData)
    }

    private fun createUniqueFileName(currentFiles : List<TemplateFile>, index : Int = 0) : String{
        val index = index
        val indexString = "{index}"
        val filePath = "${fileUtil.getPathSeparator()}newFile$indexString.txt"
        for (file in currentFiles) {
            if(file.path == filePath.replace(indexString, index.toString())){
                return createUniqueFileName(currentFiles, index +1)
            }
        }
        return filePath.replace(indexString, index.toString())
    }

    fun addNewTemplateFile(template : Template) {
        val newTemplateFile = TemplateFile(
            path = createUniqueFileName(template.files),
            pathDestination = ""
        )
        fileUtil.createFile(getTemplateFilePath(template,newTemplateFile))
        val files = template.files.toMutableList()
        files.add(newTemplateFile)
        templateMap[template.id]?.files = files
        templateListLoader.save(templateMap.values.toList())
        templates.tryEmit(templateMap.values.toList())
    }

    fun updateTemplateFile(templateId : String, currentPath : String, newTemplateFile : TemplateFile, fileData : String) : Boolean{
        val template = templateMap[templateId] ?: return false
        var files = template.files.toMutableList()

        val fileIndex = template.files.indexOfFirst{it.path == currentPath}
        if (fileIndex != -1){
            moveTemplateFile(template, template.files[fileIndex], newTemplateFile)
            files[fileIndex] = newTemplateFile
        } else  {
            files.add(newTemplateFile)
        }
        writeTemplateFile(template, newTemplateFile, fileData)
        templateMap[templateId]?.files = files

        templateListLoader.save(templateMap.values.toList())
        templates.tryEmit(templateMap.values.toList())
        return true
    }

    fun deleteTemplateFile(templateId : String, templateFile : TemplateFile) : Boolean{
        val template = templateMap[templateId] ?: return false
        var files = template.files.toMutableList()

        val fileIndex = template.files.indexOfFirst{it.path == templateFile.path}
        if (fileIndex != -1){
            files.removeAt(fileIndex)
            fileUtil.deleteFile(Path.of(templateUserDir+template.dirPath+templateFile.path))
        }
        else {
            return false
        }
        templateMap[templateId]?.files = files

        templateListLoader.save(templateMap.values.toList())
        templates.tryEmit(templateMap.values.toList())
        return true
    }

    private fun loadLocal(forceReload : Boolean = false){
        if(loaded && !forceReload) return
        try {
            templateMap.clear()
            templateListLoader.load().forEach {
                templateMap[it.id] = osPathConverter.convertPathsToOsFormat(it)
            }
        } catch (t : Throwable){
            logger.error("TemplateRepo ${t.message}")
        }
    }

    private val pathSeparator = fileUtil.getPathSeparator()
    private val templateUserDir = System.getProperty("user.home") +pathSeparator+ "CodeTool"
}