package com.jaehl.codeTool.util

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


interface FileUtil {
    fun createDirectory(path : Path)
    fun deleteDirectory(path : Path)
    fun createFile(path : Path)
    fun deleteFile(path : Path)
    fun loadFile(path : Path) : String
    fun writeFile(path : Path, date : String) : Boolean
    fun getRootDirectories() : List<String>
    fun getUserDir() : String

    fun moveFile(src: Path, dest: Path) : Boolean

    fun getPathSeparator() : String


}
class FileUtilImp(
    private val logger: Logger
) : FileUtil{
    override fun createDirectory(path : Path) {
        if(!Files.exists(path)) {
            Files.createDirectory(path)
        }
    }

    override fun deleteDirectory(path : Path) {
        Files.delete(path)
    }

    override fun createFile(path: Path) {
        if(!Files.exists(path)) {
            Files.createFile(path)
        }
    }

    override fun deleteFile(path : Path) {
        if(Files.exists(path)) {
            Files.delete(path)
        } else {
            logger.error("$tag::deleteFile files does not exist \"${path.toAbsolutePath()}\"")
        }
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

    override fun getPathSeparator() : String {
        return File.separator
    }

    override fun moveFile(src: Path, dest: Path) : Boolean {
        return try {
            Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING)
            true
        } catch (e : IOException) {
            logger.error("$tag::moveFile ${e.message}")
            false
        }
    }

    companion object {
        private val tag = FileUtilImp::class.java.simpleName
    }
}