package com.mike.steamob.release.ui.home

import com.mike.steamob.release.data.room.SteamObEntity

data class UiState(
    val list: List<SteamObEntity> = emptyList()
)