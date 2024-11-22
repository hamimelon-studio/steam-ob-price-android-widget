package com.mike.steamob.di

import androidx.room.Room
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.data.room.SteamObDatabase
import com.mike.steamob.ui.about.AboutViewModel
import com.mike.steamob.ui.home.HomeViewModel
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
    viewModel { AboutViewModel(get()) }
}