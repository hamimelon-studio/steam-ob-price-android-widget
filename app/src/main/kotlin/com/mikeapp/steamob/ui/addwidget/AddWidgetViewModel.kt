package com.mikeapp.steamob.ui.addwidget

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeapp.steamob.R
import com.mikeapp.steamob.data.SteamPriceRepository
import com.mikeapp.steamob.data.room.SteamObEntity
import com.mikeapp.steamob.widget.SteamPriceWidgetProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddWidgetViewModel(
    private val appContext: Context,
    private val repository: SteamPriceRepository
) : ViewModel() {
    private val _steamObEntity = MutableStateFlow<SteamObEntity?>(null)
    val steamObEntity: StateFlow<SteamObEntity?> = _steamObEntity

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun load(appWidgetId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _steamObEntity.value = repository.getSteamObEntity(appWidgetId)
            _isLoading.value = false
        }
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