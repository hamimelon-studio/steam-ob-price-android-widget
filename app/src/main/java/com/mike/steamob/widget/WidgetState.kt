package com.mike.steamob.widget

data class WidgetState(
    val timeUpdated: String,
    val name: String,
    val discount: String,
    val discountLevel: DiscountLevel,
    val initialPrice: String,
    val price: String
)

enum class DiscountLevel {
    None, Major, Minor
}