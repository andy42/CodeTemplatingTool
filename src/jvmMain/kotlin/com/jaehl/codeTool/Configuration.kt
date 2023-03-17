package com.jaehl.codeTool

interface Configuration {
    fun getProjectUserDir() : String
    fun getTemplateListFile() : String
    fun getProjectListFile() : String
}