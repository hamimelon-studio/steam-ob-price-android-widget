package com.mike.steamob.ui

import com.mike.steamob.data.room.SteamObEntity

data class UiState(
    val list: List<SteamObEntity> = emptyList()
)