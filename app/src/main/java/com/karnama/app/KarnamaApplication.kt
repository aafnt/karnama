package com.karnama.app

import android.app.Application
import com.karnama.app.di.AppContainer

class KarnamaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
