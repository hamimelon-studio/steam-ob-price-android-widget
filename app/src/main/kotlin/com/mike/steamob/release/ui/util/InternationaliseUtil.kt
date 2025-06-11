package com.mike.steamob.release.ui.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object InternationaliseUtil {
    fun formatCurrency(priceInCents: Long): String {
        val countryCode = Locale.getDefault().country.lowercase()
        val locale = Locale("", countryCode.uppercase())
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)

        try {
            currencyFormatter.currency = Currency.getInstance(locale)
        } catch (e: Exception) {
            currencyFormatter.currency = Currency.getInstance("USD")
        }

        val price = priceInCents / 100.0
        return currencyFormatter.format(price)
    }
}