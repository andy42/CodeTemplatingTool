package com.jaehl.codeTool.data.repo

import com.jaehl.codeTool.data.local.ObjectListLoader
import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class TemplateRepo(
    private val logger: Logger,
    private val templateListLoader : ObjectListLoader<Template>,
    private val osPathConverter : OsPathConverter
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

    fun updateTemplate(template : Template){
        templateMap[template.id] = template
        templateListLoader.save(templateMap.values.toList())
        templates.tryEmit(templateMap.values.toList())
    }

    private fun createNewId() : String {
        var id = 0
        while (true){
            if(!templateMap.containsKey(id.toString())){
                return id.toString()
            }
            id++
        }
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
}