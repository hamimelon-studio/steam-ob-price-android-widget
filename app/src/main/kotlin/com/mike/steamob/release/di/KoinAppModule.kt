package com.mike.steamob.release.di

import androidx.room.Room
import com.mike.steamob.release.data.SteamPriceRepository
import com.mike.steamob.release.data.room.SteamObDatabase
import com.mike.steamob.release.ui.about.AboutViewModel
import com.mike.steamob.release.ui.addwidget.AddWidgetViewModel
import com.mike.steamob.release.ui.addwidget.mainflow.AddWidgetDialogViewModel
import com.mike.steamob.release.ui.home.HomeViewModel
import com.mike.steamob.release.widget.SteamPriceWidgetProviderUseCase
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
    viewModel { AddWidgetViewModel(get()) }

    factory { SteamPriceWidgetProviderUseCase(get()) }
}