package com.jaehl.codeTool.ui.page.templateEdit

import com.jaehl.codeTool.data.model.Template
import com.jaehl.codeTool.data.repo.TemplateRepo
import com.jaehl.codeTool.util.FileUtil

interface TemplateEditValidator {
    fun setValidatorListener(listener : TemplateEditValidatorListener)
    fun validateTemplate(currentTemplate : Template?, name : String, dirPath : String) : Boolean
    fun validateTemplateName(currentTemplate : Template?, name : String) : Boolean
    fun validateTemplateDir(currentTemplate : Template?, dirPath : String) : Boolean

    fun validateTemplateFile(filePaths : List<String>, currentPath : String, path : String, pathDestination : String) : Boolean
    fun validateTemplateFilePath(filePaths : List<String>, currentPath : String, path : String) : Boolean
    fun validateTemplateFilePathDestination(pathDestination : String) : Boolean
}

interface TemplateEditValidatorListener {
    fun onTemplateNameError(error : String)
    fun onTemplateDirError(error : String)

    fun onTemplateFilePathError(error : String)
    fun onTemplateFilePathDestinationError(error : String)
}

class TemplateEditValidatorImp(
    private val fileUtil: FileUtil,
    private val templateRepo: TemplateRepo
) : TemplateEditValidator {

    private var listener : TemplateEditValidatorListener? = null

    override fun setValidatorListener(listener : TemplateEditValidatorListener) {
        this.listener = listener
    }

    override fun validateTemplate(
        currentTemplate : Template?,
        name : String,
        dir : String
    ) : Boolean {
        return validateTemplateName(currentTemplate, name) && validateTemplateDir(currentTemplate, dir)
    }

    override fun validateTemplateName(currentTemplate : Template?, name : String) : Boolean {
        if (name.isEmpty()) {
            listener?.onTemplateNameError("field empty")
            return false
        }
        else if (currentTemplate?.name != name && templateRepo.templateNameExist(name)) {
            listener?.onTemplateNameError("name already in use")
            return false
        }
        return true
    }

    override fun validateTemplateDir(currentTemplate : Template?, dirPath : String) : Boolean {
        if (dirPath.isEmpty()) {
            listener?.onTemplateDirError("field empty")
            return false
        }
        else if (dirPath[0].toString() != fileUtil.getPathSeparator()) {
            listener?.onTemplateDirError("most start with a \"${fileUtil.getPathSeparator()}\"")
            return false
        }
        else if (currentTemplate?.dirPath != dirPath && templateRepo.templateDirPathExist(dirPath)) {
            listener?.onTemplateDirError("directory already in use")
            return false
        }

        return true
    }

    override fun validateTemplateFile(filePaths : List<String>, currentPath : String, path : String, pathDestination : String) : Boolean {
        return validateTemplateFilePath(filePaths, currentPath, path) && validateTemplateFilePathDestination(pathDestination)
    }

    override fun validateTemplateFilePath(filePaths : List<String>, currentPath : String,  path : String) : Boolean {
        if(path.isEmpty()) {
            listener?.onTemplateFilePathError("field empty")
            return false
        }
        else if (path[0].toString() != fileUtil.getPathSeparator()) {
            listener?.onTemplateFilePathError("most start with a \"${fileUtil.getPathSeparator()}\"")
            return false
        }
        else if(currentPath != path && filePaths.contains(path)) {
            listener?.onTemplateFilePathError("file name already exists")
            return false
        }
        return true
    }

    override fun validateTemplateFilePathDestination(pathDestination : String ) : Boolean {
        if(pathDestination.isEmpty()) {
            listener?.onTemplateFilePathDestinationError("field empty")
            return false
        }
        return true
    }
}