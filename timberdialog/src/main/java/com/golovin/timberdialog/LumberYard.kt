package com.golovin.timberdialog

import android.content.Context
import com.golovin.timberdialog.util.SingletonHolder
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

private const val BUFFER_SIZE = 200
private const val LOGS_DIR_NAME = "logs"
private const val LOG_FILE_EXTENSION = ".log"

class LumberYard(private val context: Context) {
    private val entries = ArrayDeque<LogEntry>(BUFFER_SIZE + 1)
    private val entrySubject = PublishSubject.create<LogEntry>()

    private val dateFormatter = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue() = SimpleDateFormat("MM-dd kk:mm:ss.S", Locale.US)
    }

    companion object : SingletonHolder<LumberYard, Context>(::LumberYard)

    fun tree() = object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            addEntry(LogEntry(timestamp(), priority, tag, message))
        }
    }

    private fun timestamp(): String = dateFormatter.get().format(Date())

    private fun addEntry(entry: LogEntry) {
        synchronized(this) {
            entries += entry

            if (entries.size > BUFFER_SIZE) {
                entries.removeFirst()
            }

            entrySubject.onNext(entry)
        }
    }

    fun bufferedLogs(): List<LogEntry> = entries.toList()

    fun logs(): Observable<LogEntry> = entrySubject

    fun save(): Single<File> = Single.fromCallable {
        val dir = logsDir()

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, createLogFilename())

        FileWriter(file, true).use {
            val entries = bufferedLogs()
            for (entry in entries) {
                it.write(entry.prettyPrint() + "\n")
            }
        }

        return@fromCallable file
    }

    fun cleanUp() {
        Completable.fromAction {
            logsDir().listFiles()?.forEach {
                if (it.name.endsWith(LOG_FILE_EXTENSION)) {
                    it.delete()
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .onErrorComplete()
                .subscribe()
    }

    private fun logsDir() = File(context.getExternalFilesDir(null), LOGS_DIR_NAME)

    private fun createLogFilename(): String {
        val currentDate = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US).format(Calendar.getInstance().time)

        return "$currentDate$LOG_FILE_EXTENSION"
    }
}

