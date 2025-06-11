package com.mike.steamob.release.ui.commonui

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController

object SystemUiUtil {
    fun applySystemBarStyle(activity: Activity, forceDark: Boolean) {
        with(activity) {
            val isDarkTheme =
                forceDark || when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> true
                    else -> false
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.statusBarColor = if (isDarkTheme) Color.BLACK else Color.WHITE
                window.navigationBarColor = if (isDarkTheme) Color.BLACK else Color.WHITE

                window.insetsController?.setSystemBarsAppearance(
                    if (!isDarkTheme)
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    else
                        0, // Clear light appearance = default light icons
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = when {
                    !isDarkTheme -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    else -> 0 // light icons
                }

                @Suppress("DEPRECATION")
                window.statusBarColor = if (isDarkTheme) Color.BLACK else Color.WHITE
                @Suppress("DEPRECATION")
                window.navigationBarColor = if (isDarkTheme) Color.BLACK else Color.WHITE
            }
        }
    }
}