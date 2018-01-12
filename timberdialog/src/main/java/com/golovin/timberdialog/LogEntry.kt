package com.golovin.timberdialog

import android.util.Log

class LogEntry(val level: Int, val tag: String?, val message: String) {

    val displayLevel: String
        get() = when (level) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "?"
        }

    fun prettyPrint() = String.format("%18s %s %s", tag, displayLevel, message)
}