package com.mike.steamob

import android.app.Application
import com.mike.steamob.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class SteamObApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SteamObApplication)
            modules(appModule)
        }
    }
}