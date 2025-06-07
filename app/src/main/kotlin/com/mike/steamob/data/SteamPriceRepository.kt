package com.mike.steamob.data

import com.mike.steamob.data.room.SteamAppState
import com.mike.steamob.data.room.SteamObDao
import com.mike.steamob.data.room.SteamObEntity
import com.mike.steamob.ui.home.UiState
import java.util.Locale
import kotlin.math.roundToInt

class SteamPriceRepository(
    private val dao: SteamObDao
) {
    suspend fun fetchApp(input: SteamObEntity): SteamObEntity? {
        if (input.appId.isEmpty()) {
            return null
        }
        val countryCode = Locale.getDefault().country.lowercase()
        val languageCode = Locale.getDefault().language.lowercase()
        val responses = apiService.getAppDetails(input.appId, countryCode, languageCode)
        val response = responses.entries.firstOrNull()?.value
        if (response != null) {
            if (response.success && response.data != null) {
                val data = response.data
                val state = when {
                    data.price_overview != null -> SteamAppState.Normal
                    data.release_date?.coming_soon == true -> SteamAppState.ComingSoon
                    else -> SteamAppState.NotAvailable
                }
                val adjustDiscount = data.price_overview?.let {
                    adjustDiscountValue(
                        data.price_overview.discountPercentage,
                        data.price_overview.initial,
                        data.price_overview.final
                    )
                }
                return SteamObEntity(
                    widgetId = input.widgetId,
                    appId = input.appId,
                    appName = data.name,
                    lastUpdate = System.currentTimeMillis(),
                    rrp = data.price_overview?.initial ?: 0,
                    finalPrice = data.price_overview?.final ?: 0,
                    discount = adjustDiscount ?: 0,
                    alarmThreshold = input.alarmThreshold,
                    logo = data.capsule_imagev5,
                    state = state.displayName
                )
            }
        }
        return null
    }

    suspend fun getSteamObEntity(widgetId: Int): SteamObEntity? {
        return dao.getObApps(widgetId)
    }

    suspend fun getAllEmptySteamObEntitiesByTimeStampDesc(): UiState {
        return UiState(
            dao.getAllObApps()
                .filter {
                    it.appId.isEmpty()
                }.sortedByDescending {
                    it.lastUpdate
                }
        )
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