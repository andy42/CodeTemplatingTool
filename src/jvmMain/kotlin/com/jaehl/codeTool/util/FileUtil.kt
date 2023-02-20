package com.jaehl.codeTool.util

import java.io.File
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists


interface FileUtil {
    fun createDirectory(path : Path)
    fun createFile(path : Path)
    fun loadFile(path : Path) : String
    fun writeFile(path : Path, date : String) : Boolean
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
}