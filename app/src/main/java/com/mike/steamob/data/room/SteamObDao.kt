package com.mike.steamob.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SteamObDao {
    @Query("SELECT * FROM steam_ob WHERE widgetId = :widgetId")
    fun getObApps(widgetId: String): Flow<SteamObEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(steamObEntity: SteamObEntity)

    @Query("DELETE FROM steam_ob")
    suspend fun clear()

    @Query("SELECT * FROM steam_ob")
    suspend fun getAllObApps(): List<SteamObEntity>
}