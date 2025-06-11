package com.mike.steamob.release.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SteamObEntity::class
    ], version = 1
)
abstract class SteamObDatabase : RoomDatabase() {

    abstract fun steamObDao(): SteamObDao
}