package com.mike.steamob.ui.addwidget

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.mike.steamob.R
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.data.room.SteamObEntity
import com.mike.steamob.widget.SteamPriceWidgetProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AddWidgetViewModel(
    private val appContext: Context,
    private val repository: SteamPriceRepository
) : ViewModel() {
    suspend fun loadEntity(appWidgetId: Int): SteamObEntity? {
        return repository.getSteamObEntity(appWidgetId)
    }

    fun updateWidget(appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(appContext)
        // Create an Intent to update the widget
        val intent = Intent(appContext, SteamPriceWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        // Update the widget
        appWidgetManager.updateAppWidget(
            appWidgetId,
            RemoteViews(appContext.packageName, R.layout.widget_layout)
        )

        // Alternatively, you could call your specific update method:
        SteamPriceWidgetProvider().onUpdate(appContext, appWidgetManager, intArrayOf(appWidgetId))
    }

    fun save(
        appWidgetId: Int, appId: String, priceThreshold: Float,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(IO) {
            var success = false
            try {
                val entity = SteamObEntity(
                    widgetId = appWidgetId,
                    appId = appId,
                    alarmThreshold = (priceThreshold * 100).toLong()
                )
                val outputEntity = repository.fetchApp(entity)
                repository.update(outputEntity ?: entity)
                success = outputEntity != null
            } finally {
                withContext(Main) {
                    onComplete(success)
                }
            }
        }
    }

    fun finishWithResult(appWidgetId: Int, success: Boolean, onFinish: (Int, Intent) -> Unit) {
        val result = if (success) RESULT_OK else RESULT_CANCELED
        val resultIntent = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        onFinish.invoke(result, resultIntent)
    }
}