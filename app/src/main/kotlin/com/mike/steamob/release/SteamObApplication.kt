package com.mike.steamob.release

import android.app.Application
import com.mike.steamob.release.di.appModule
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