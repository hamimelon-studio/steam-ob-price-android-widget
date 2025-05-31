package com.mikeapp.steamob.ui.home

import com.mikeapp.steamob.data.room.SteamObEntity

data class UiState(
    val list: List<SteamObEntity> = emptyList()
)