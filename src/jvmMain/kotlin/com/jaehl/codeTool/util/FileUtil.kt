package com.jaehl.codeTool.util

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists


interface FileUtil {
    fun createDirectory(path : Path)
    fun renameDirectory(oldPath : Path, newPath : Path) : Boolean
    fun deleteDirectory(path : Path)
    fun createFile(path : Path)
    fun deleteFile(path : Path)
    fun loadFile(path : Path) : String
    fun writeFile(path : Path, date : String) : Boolean
    fun fileExists(path : Path) : Boolean
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

    override fun renameDirectory(oldPath : Path, newPath : Path) : Boolean{
        return try {
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING)
            true
        } catch (e : IOException) {
            logger.error("$tag::renameDirectory ${e.message}")
            false
        }
    }

    override fun deleteDirectory(path : Path) {
        if(Files.exists(path)) {
            Files.delete(path)
        }
    }

    override fun createFile(path: Path) {
        walkCreateFolders(path.toFile().absolutePath.split(getPathSeparator()).dropLast(1))
        if(!Files.exists(path)) {
            Files.createFile(path)
        }
    }

    override fun deleteFile(path : Path) {
        if(Files.exists(path)) {
            Files.delete(path)
            walkDeleteEmptyFolders(path.toFile().absolutePath.split(getPathSeparator()).dropLast(1))
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

    override fun fileExists(path : Path) : Boolean {
        return path.exists()
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

    private fun walkCreateFolders(path: List<String>, index : Int = 0){
        if(index >= path.size) return
        val folder = Path.of(path.subList(0, index+1).joinToString(getPathSeparator()))
        if(!Files.exists(folder)){
            Files.createDirectory(folder)
        }
        walkCreateFolders(path, index+1)
    }

    @Throws(IOException::class)
    private fun isDirEmpty(directory: Path): Boolean {
        Files.newDirectoryStream(directory).use { dirStream -> return !dirStream.iterator().hasNext() }
    }

    private fun walkDeleteEmptyFolders(path: List<String>, index : Int = (path.size -1)){
        if(index < 0) return
        val folder = Path.of(path.subList(0, index+1).joinToString(getPathSeparator()))
        if(Files.exists(folder) && isDirEmpty(folder)){
            deleteDirectory(folder)
        }
        walkDeleteEmptyFolders(path, index-1)
    }

    override fun moveFile(src: Path, dest: Path) : Boolean {
        return try {
            walkCreateFolders(dest.toFile().absolutePath.split(getPathSeparator()).dropLast(1))
            Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING)
            walkDeleteEmptyFolders(src.toFile().absolutePath.split(getPathSeparator()).dropLast(1))
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