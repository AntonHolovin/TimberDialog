package com.golovin.timberdialog.sample

import android.app.Application
import com.golovin.timberdialog.LumberYard
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        LumberYard.getInstance(this).let {
            it.cleanUp()
            Timber.plant(it.tree())
        }
    }
}