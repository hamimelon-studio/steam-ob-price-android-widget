package com.mike.steamob.release.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SteamObDao {
    @Query("SELECT * FROM steam_ob WHERE widgetId = :widgetId")
    suspend fun getObApps(widgetId: Int): SteamObEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(steamObEntity: SteamObEntity)

    @Query("DELETE FROM steam_ob WHERE widgetId = :widgetId")
    suspend fun removeObApp(widgetId: Int)

    @Query("DELETE FROM steam_ob")
    suspend fun clear()

    @Query("SELECT * FROM steam_ob")
    suspend fun getAllObApps(): List<SteamObEntity>
}