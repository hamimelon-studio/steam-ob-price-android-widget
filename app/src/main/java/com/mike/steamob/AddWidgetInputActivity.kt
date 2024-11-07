package com.mike.steamob

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mike.steamob.widget.SteamPriceWidgetProvider

class AddWidgetInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        showAppIdDialog(appWidgetId)
    }

    private fun showAppIdDialog(appWidgetId: Int) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // App ID input field
        val appIdInput = EditText(this).apply {
            hint = "Enter App ID"
        }

        // Price threshold input field
        val priceThresholdInput = EditText(this).apply {
            hint = "Enter Price Threshold"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(appIdInput)
        layout.addView(priceThresholdInput)

        AlertDialog.Builder(this)
            .setTitle("Enter App ID and Price Threshold")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val appId = appIdInput.text.toString()
                val priceThreshold = priceThresholdInput.text.toString().toFloatOrNull()
                    ?: 0f // Default to 0 if invalid input

                // Save app ID and price threshold or pass them back to the widget
                saveAppId(appWidgetId, appId)
                savePriceThreshold(
                    appWidgetId,
                    priceThreshold
                ) // New function to save price threshold
                updateWidget(appWidgetId)
                finish() // Close activity after input
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish() // Close activity if canceled
            }
            .show()
    }

    private fun updateWidget(appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        // Create an Intent to update the widget
        val intent = Intent(this, SteamPriceWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        // Update the widget
        appWidgetManager.updateAppWidget(
            appWidgetId,
            RemoteViews(packageName, R.layout.widget_layout)
        )

        // Alternatively, you could call your specific update method:
        SteamPriceWidgetProvider().onUpdate(this, appWidgetManager, intArrayOf(appWidgetId))
    }

    private fun saveAppId(appWidgetId: Int, appId: String) {
        // Save app ID to SharedPreferences or handle as needed
        val prefs = getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val key = "appid_$appWidgetId"
        prefs.edit().putString(key, appId).apply()
    }

    private fun savePriceThreshold(appWidgetId: Int, priceThreshold: Float) {
        val sharedPreferences = getSharedPreferences("widget_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("threshold_$appWidgetId", priceThreshold)
        editor.apply()
    }
}