package com.jaehl.codeTool

class ConfigurationImp : Configuration {
    override fun getProjectUserDir(): String = "CodeTool"
    override fun getTemplateListFile(): String = "templateList.json"
    override fun getProjectListFile() : String = "projectList.json"
}