package com.mikeapp.steamob.di

import androidx.room.Room
import com.mikeapp.steamob.data.SteamPriceRepository
import com.mikeapp.steamob.data.room.SteamObDatabase
import com.mikeapp.steamob.ui.about.AboutViewModel
import com.mikeapp.steamob.ui.addwidget.AddWidgetViewModel
import com.mikeapp.steamob.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SteamPriceRepository(get(), get()) }

    single {
        Room.databaseBuilder(
            get(),
            SteamObDatabase::class.java,
            "steam_ob_db"
        ).build()
    }

    single { get<SteamObDatabase>().steamObDao() }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { AddWidgetViewModel(get(), get()) }
    viewModel { AboutViewModel(get()) }
}