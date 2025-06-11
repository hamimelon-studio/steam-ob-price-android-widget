package com.mike.steamob.release.widget

import com.mike.steamob.release.data.room.SteamAppState

data class WidgetUiState(
    val timeUpdated: String,
    val name: String,
    val discount: String,
    val price: String,
    val state: SteamAppState,
    val isAlarm: Boolean
)