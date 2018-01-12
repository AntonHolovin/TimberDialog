package com.golovin.timberdialog.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.golovin.timberdialog.ui.LogDialog
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_show_dialog).setOnClickListener {
            LogDialog().show(supportFragmentManager, LogDialog::class.java.name)
        }

        writeLogs()
    }

    private fun writeLogs() {
        Observable.intervalRange(0, 24, 1, 1, TimeUnit.SECONDS)
                .subscribe {
                    val log = (Log.VERBOSE..Log.ASSERT + 1).random()

                    when (log) {
                        Log.VERBOSE -> Timber.v("Verbose")
                        Log.DEBUG -> Timber.d("Debug")
                        Log.INFO -> Timber.i("Info")
                        Log.WARN -> Timber.w("Warning")
                        Log.ERROR -> Timber.e("Error")
                        Log.ASSERT -> Timber.wtf("Assert")
                        else -> Timber.wtf("WTF")
                    }
                }
    }

    private fun ClosedRange<Int>.random() = random.nextInt(endInclusive - start) + start
}
