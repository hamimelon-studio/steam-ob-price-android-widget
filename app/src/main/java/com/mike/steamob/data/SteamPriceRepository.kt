package com.mike.steamob.data

import com.mike.steamob.ui.DiscountLevel
import com.mike.steamob.ui.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SteamPriceRepository {
    suspend fun fetchData(appId: String): UiState? {
        val response = apiService.getAppDetails(appId)
        val data1 = response.entries.firstOrNull()?.value
        if (data1 != null) {
            val data = data1.data
            return UiState(
                timeUpdated = getCurrentTimeStampString(),
                name = data.name,
                discount = "${data.price_overview.discountPercentage}% OFF",
                discountLevel = mapDiscountLevel(data.price_overview.discountPercentage),
                initialPrice = formatAud(data.price_overview.initial),
                price = data.price_overview.final_formatted
            )
        }
        return null
    }

    private fun getCurrentTimeStampString(): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val currentTime: String = dateFormat.format(Date())
        return currentTime
    }

    private fun formatAud(price: Long): String = String.format("A\$ ${price / 100.0f}")

    private fun mapDiscountLevel(discountPercentage: Int): DiscountLevel =
        when {
            discountPercentage == 0 -> DiscountLevel.None
            discountPercentage < 50 -> DiscountLevel.Minor
            else -> DiscountLevel.Major
        }
}