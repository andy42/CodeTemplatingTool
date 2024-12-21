package com.jaehl.codeTool.data.repo

import com.jaehl.codeTool.data.local.ObjectListLoader
import com.jaehl.codeTool.data.model.Project
import com.jaehl.codeTool.ui.util.OsPathConverter
import com.jaehl.codeTool.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ProjectRepo @Inject constructor(
    private val logger: Logger,
    private val projectListLoader : ObjectListLoader<Project>,
    private val osPathConverter : OsPathConverter
) {

    private val projectMap = LinkedHashMap<String, Project>()
    private var loaded = false

    private var projects = MutableSharedFlow<List<Project>>(replay = 1)

    init {
        GlobalScope.async {
            loadLocal(true)
            projects.tryEmit(projectMap.values.toList())
        }
    }

    fun getProjects() : SharedFlow<List<Project>> = projects

    suspend fun getProject(id : String?) : Project? {
        if (id == null) return null
        return projectMap[id]
    }

    fun updateProject(project : Project) : Project{
        var newProject = project
        if(newProject.id.isEmpty()){
            newProject = newProject.copy(
                id = createNewId()
            )
        }
        projectMap[newProject.id] = newProject
        projectListLoader.save(projectMap.values.toList())
        projects.tryEmit(projectMap.values.toList())
        return newProject
    }

    fun deleteProject(id : String) {
        projectMap.remove(id)
        projectListLoader.save(projectMap.values.toList())
        projects.tryEmit(projectMap.values.toList())
    }

    private fun createNewId() : String {
        var id = 0
        while (true){
            if(!projectMap.containsKey(id.toString())){
                return id.toString()
            }
            id++
        }
    }

    private fun loadLocal(forceReload : Boolean = false){
        if(loaded && !forceReload) return
        try {
            projectMap.clear()
            projectListLoader.load().forEach {
                projectMap[it.id] = osPathConverter.convertPathsToOsFormat(it)
            }
        } catch (t : Throwable){
            logger.error("ProjectRepo ${t.message}")
        }
    }
}