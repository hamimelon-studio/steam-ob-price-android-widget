package com.mike.steamob.data

import android.content.Context
import com.mike.steamob.R
import com.mike.steamob.data.room.SteamObDao
import com.mike.steamob.data.room.SteamObEntity
import com.mike.steamob.ui.home.UiState
import kotlin.math.roundToInt

class SteamPriceRepository(
    private val appContext: Context,
    private val dao: SteamObDao
) {
    suspend fun fetchApp(input: SteamObEntity): SteamObEntity? {
        val currency = appContext.getString(R.string.api_currency)
        val language = appContext.getString(R.string.api_lang)
        val responses = apiService.getAppDetails(input.appId, currency, language)
        val response = responses.entries.firstOrNull()?.value
        if (response != null) {
            if (response.success && response.data != null) {
                val data = response.data
                val adjustDiscount = adjustDiscountValue(
                    data.price_overview.discountPercentage,
                    data.price_overview.initial,
                    data.price_overview.final
                )
                return SteamObEntity(
                    widgetId = input.widgetId,
                    appId = input.appId,
                    appName = data.name,
                    lastUpdate = System.currentTimeMillis(),
                    rrp = data.price_overview.initial,
                    finalPrice = data.price_overview.final,
                    discount = adjustDiscount,
                    alarmThreshold = input.alarmThreshold,
                    logo = data.capsule_imagev5
                )
            }
        }
        return null
    }

    suspend fun getSteamObEntity(widgetId: Int): SteamObEntity? {
        return dao.getObApps(widgetId)
    }

    suspend fun getSteamObEntities(): UiState {
        return UiState(
            dao.getAllObApps()
        )
    }

    suspend fun update(entity: SteamObEntity) {
        dao.save(entity)
    }

    suspend fun delete(widgetId: Int) {
        dao.removeObApp(widgetId)
    }

    private fun adjustDiscountValue(discountGiven: Int, initialPrice: Long, finalPrice: Long): Int {
        if (discountGiven > 0) {
            return discountGiven
        }
        val adjusted = (initialPrice - finalPrice) * 1.0f / initialPrice * 100.0f
        return adjusted.roundToInt()
    }
}