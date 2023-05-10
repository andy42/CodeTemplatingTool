package com.jaehl.codeTool

import javax.inject.Inject

class ConfigurationImp @Inject constructor(): Configuration {
    override fun getProjectUserDir(): String = "CodeTool"
    override fun getTemplateListFile(): String = "templateList.json"
    override fun getProjectListFile() : String = "projectList.json"
}