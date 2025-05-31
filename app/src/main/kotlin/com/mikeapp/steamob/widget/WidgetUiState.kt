package com.mikeapp.steamob.widget

import com.mikeapp.steamob.data.room.SteamAppState

data class WidgetUiState(
    val timeUpdated: String,
    val name: String,
    val discount: String,
    val price: String,
    val state: SteamAppState,
    val isAlarm: Boolean
)