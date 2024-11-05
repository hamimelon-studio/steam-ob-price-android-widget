package com.mike.steamob

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
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
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter App ID")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val appId = input.text.toString()
                // Save app ID or pass it back to widget
                saveAppId(appWidgetId, appId)
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
        val key = "widget_id_$appWidgetId"
        prefs.edit().putString(key, appId).apply()
    }
}