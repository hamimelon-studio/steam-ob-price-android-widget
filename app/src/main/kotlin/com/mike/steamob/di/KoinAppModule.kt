package com.mike.steamob.di

import androidx.room.Room
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.data.room.SteamObDatabase
import com.mike.steamob.ui.about.AboutViewModel
import com.mike.steamob.ui.addwidget.AddWidgetViewModel
import com.mike.steamob.ui.addwidget.mainflow.AddWidgetDialogViewModel
import com.mike.steamob.ui.home.HomeViewModel
import com.mike.steamob.widget.SteamPriceWidgetProviderUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SteamPriceRepository(get()) }

    single {
        Room.databaseBuilder(
            get(),
            SteamObDatabase::class.java,
            "steam_ob_db"
        ).build()
    }

    single { get<SteamObDatabase>().steamObDao() }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { AddWidgetDialogViewModel(get(), get()) }
    viewModel { AboutViewModel(get()) }
    viewModel { AddWidgetViewModel(get(), get()) }

    factory { SteamPriceWidgetProviderUseCase(get()) }
}