package com.jaehl.codeTool.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Path


interface FileUtil {
    fun createDirectory(path : Path)
    fun createFile(path : Path)
    fun loadFile(path : Path) : String
    fun writeFile(path : Path, date : String) : Boolean
    fun getRootDirectories() : List<String>
    fun getUserDir() : String
}
class FileUtilImp : FileUtil{
    override fun createDirectory(path : Path) {
        Files.createDirectory(path)
    }

    override fun createFile(path: Path) {
        Files.createFile(path)
    }

    override fun loadFile(path : Path) : String {
        return try {
            path.toFile().readText()
        } catch (t: Throwable) {
            "failed to load File"
        }
    }

    override fun writeFile(path : Path, date : String) : Boolean {

        path.toFile().printWriter().use { writer ->
            writer.write(date)
        }

        return true
    }

    override fun getRootDirectories() : List<String> {
        return File.listRoots().map { it.absolutePath }
    }

    override fun getUserDir() : String {
        return System.getProperty("user.home")
    }
}