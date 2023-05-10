package com.jaehl.codeTool.util

import javax.inject.Inject

class Logger @Inject constructor () {
    fun log(message : String) {
        println("Log : $message")
    }
    fun error(message : String) {
        println("Log : $message")
    }
}