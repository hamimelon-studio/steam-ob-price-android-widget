package com.mike.steamob.release.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import com.mike.steamob.release.data.SteamPriceRepository
import com.mike.steamob.release.data.room.SteamObEntity
import com.mike.steamob.release.ui.addwidget.AddWidgetInputActivity
import com.mike.steamob.release.ui.addwidget.CustomisedIntentConstant.ACTION_WIDGET_GEAR_CONFIG_ENTRY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SteamPriceWidgetProviderUseCase(private val repository: SteamPriceRepository) {
    fun getSetupConfigIntent(context: Context, appWidgetId: Int): Intent {
        val intent = Intent(context, AddWidgetInputActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = ACTION_WIDGET_GEAR_CONFIG_ENTRY
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return intent
    }

    fun deleteWidgets(widgetIdList: List<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            widgetIdList.forEach {
                repository.delete(it)
            }
        }
    }

    fun saveWidgetIfNewCreated(widgetId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = repository.getSteamObEntity(widgetId)
            if (entity == null) {
                val newEntity = SteamObEntity(
                    widgetId = widgetId,
                    lastUpdate = System.currentTimeMillis()
                )
                repository.update(newEntity)
            }
        }
    }

    fun asyncFetchEntityByWidgetId(widgetId: Int, callback: (SteamObEntity?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = getSteamObEntity(widgetId)
            val entityResult = entity?.let {
                fetchApp(entity)
            }
            callback.invoke(entityResult)
        }
    }

    private suspend fun getSteamObEntity(widgetId: Int): SteamObEntity? {
        return repository.getSteamObEntity(widgetId)
    }

    private suspend fun fetchApp(input: SteamObEntity): SteamObEntity? {
        return repository.fetchApp(input)
    }
}