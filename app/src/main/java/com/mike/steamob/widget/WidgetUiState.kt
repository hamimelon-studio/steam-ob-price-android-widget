package com.mike.steamob.widget

import com.mike.steamob.data.room.SteamAppState

data class WidgetUiState(
    val timeUpdated: String,
    val name: String,
    val discount: String,
    val price: String,
    val state: SteamAppState
)