package com.mike.steamob.release.data.room

enum class SteamAppState(val displayName: String) {
    Normal("Normal"),
    ComingSoon("Coming Soon!"),
    NotAvailable("Not Available");

    companion object {
        fun getStateFromName(displayName: String): SteamAppState {
            return when (displayName) {
                Normal.displayName -> Normal
                ComingSoon.displayName -> ComingSoon
                else -> NotAvailable
            }
        }
    }
}