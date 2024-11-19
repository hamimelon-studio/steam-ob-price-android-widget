package com.mike.steamob.data

import com.mike.steamob.data.room.SteamObDao
import com.mike.steamob.data.room.SteamObDatabase
import com.mike.steamob.data.room.SteamObEntity
import com.mike.steamob.ui.UiState
import com.mike.steamob.widget.DiscountLevel
import com.mike.steamob.widget.WidgetState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class SteamPriceRepository(private val dao: SteamObDao) {
    suspend fun fetchAllApps(): UiState {
        return UiState(
            dao.getAllObApps()
        )
    }

    suspend fun fetchApp(input: SteamObEntity): SteamObEntity? {
        val response = apiService.getAppDetails(input.appId)
        val data1 = response.entries.firstOrNull()?.value
        if (data1 != null) {
            val data = data1.data
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
                rrp = data.price_overview.initial.toFloat(),
                finalPrice = data.price_overview.final.toFloat(),
                discount = adjustDiscount,
                alarmThreshold = input.alarmThreshold
            )
        }
        return null
    }

    suspend fun update(entity: SteamObEntity) {
        dao.save(entity)
    }

    private fun adjustDiscountValue(discountGiven: Int, initialPrice: Long, finalPrice: Long): Int {
        if (discountGiven > 0) {
            return discountGiven
        }
        val adjusted = (initialPrice - finalPrice) * 1.0f / initialPrice * 100.0f
        return adjusted.roundToInt()
    }
}