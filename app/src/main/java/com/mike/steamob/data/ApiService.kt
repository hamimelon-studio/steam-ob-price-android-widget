package com.mike.steamob.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/appdetails/")
    suspend fun getAppDetails(@Query("appids") appId: String): Map<String, SteamPrice>
}

// Create the API service
val apiService: ApiService = retrofit.create(ApiService::class.java)

data class SteamPrice(
    val success: Boolean,
    val data: SteamPriceData
)

data class SteamPriceData(
    val steam_appid: String,
    val name: String,
    val price_overview: SteamPriceOverview
)

data class SteamPriceOverview(
    val currency: String,
    val initial: Long,
    val final: Long,
    val discountPercentage: Int,
    val final_formatted: String
)
