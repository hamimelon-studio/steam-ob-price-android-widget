package com.mike.steamob.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steam_ob")
data class SteamObEntity(
    @PrimaryKey
    val widgetId: Int,
    val appId: String = "",
    val appName: String = "",
    val lastUpdate: Long = -1,
    val rrp: Long = 0,
    val finalPrice: Long = 0,
    val discount: Int = 0,
    val alarmThreshold: Float = Float.MAX_VALUE
)