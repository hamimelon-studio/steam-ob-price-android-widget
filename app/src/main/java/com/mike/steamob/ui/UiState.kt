package com.mike.steamob.ui

data class UiState(
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