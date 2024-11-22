package com.mike.steamob.data.model

data class SteamPriceResponse(
    val success: Boolean,
    val data: SteamPriceData?
)

data class SteamPriceData(
    val steam_appid: String,
    val name: String,
    val capsule_imagev5: String,
    val price_overview: SteamPriceOverview
)

data class SteamPriceOverview(
    val currency: String,
    val initial: Long,
    val final: Long,
    val discountPercentage: Int,
    val final_formatted: String
)
