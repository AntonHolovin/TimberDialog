package com.golovin.timberdialog

import android.util.Log

class LogEntry(val timestamp: String, val level: Int, val tag: String?, val message: String) {

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

    fun prettyPrint() = "%16s %8s %s %s".format(timestamp, tag ?: "", displayLevel, message)
}