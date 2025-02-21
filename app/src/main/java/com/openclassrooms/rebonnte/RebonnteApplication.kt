package com.openclassrooms.rebonnte

import android.app.Application
import com.openclassrooms.rebonnte.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RebonnteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RebonnteApplication)
            modules(appModule)
        }
    }
}