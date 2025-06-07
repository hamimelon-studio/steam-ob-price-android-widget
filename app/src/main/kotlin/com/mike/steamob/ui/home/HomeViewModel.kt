package com.mike.steamob.ui.home

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.ui.addwidget.AddWidgetInputActivity
import com.mike.steamob.ui.addwidget.CustomisedIntentConstant.ACTION_IN_APP_ADD_WIDGET_ENTRY
import com.mike.steamob.ui.addwidget.CustomisedIntentConstant.ACTION_IN_APP_GEAR_CONFIG_ENTRY
import com.mike.steamob.ui.addwidget.CustomisedIntentConstant.ACTION_IN_APP_TILE_CONFIG_ENTRY
import com.mike.steamob.widget.SteamPriceWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val appContext: Context,
    private val repository: SteamPriceRepository
) : ViewModel() {
    val isRefresh = MutableStateFlow(false)

    val uiState = MutableStateFlow<UiState?>(null)

    init {
        cleanUpInactiveWidgets {
            forceRefresh()
        }
    }

    fun forceRefresh() {
        viewModelScope.launch(IO) {
            try {
                isRefresh.value = true
                val list = repository.getSteamObEntities()
                Log.d("HomeScreen", "list.size: ${list.list.size}")
                list.list.forEach {
                    Log.d("HomeScreen", " game tile: $it")
                }
                uiState.value = list
            } catch (e: Exception) {
                Log.d("HomeScreen", "Exception: $e")
                e.printStackTrace()
                uiState.value = null
            } finally {
                delay(100)
                withContext(Dispatchers.Main) {
                    isRefresh.value = false
                }
            }
        }
    }

    fun requestPinWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetProvider = ComponentName(context, SteamPriceWidgetProvider::class.java)
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val successCallback = PendingIntent.getActivity(
                context,
                0,
                Intent(context, AddWidgetInputActivity::class.java).apply {
                    action = ACTION_IN_APP_ADD_WIDGET_ENTRY
                    Log.d("Lifecycle", "requestPinWidget")
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            appWidgetManager.requestPinAppWidget(widgetProvider, null, successCallback)
        }
    }

    fun launchAddWidgetInputActivity(appWidgetId: Int, isNew: Boolean) {
        val intentAction = if (isNew) {
            ACTION_IN_APP_TILE_CONFIG_ENTRY
        } else {
            ACTION_IN_APP_GEAR_CONFIG_ENTRY
        }
        val intent = getSetupConfigIntent(appContext, appWidgetId, intentAction)
        appContext.startActivity(intent)
    }

    private fun getSetupConfigIntent(context: Context, appWidgetId: Int, entryIntentAction: String): Intent {
        val intent = Intent(context, AddWidgetInputActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = entryIntentAction
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return intent
    }

    private fun cleanUpInactiveWidgets(onComplete: () -> Unit) {
        viewModelScope.launch(IO) {
            val uiState = repository.getSteamObEntities()
            val activeWidgetList = getActiveWidgetList()
            Log.d("HomeScreen", "allWidget in db: ${uiState.list.map { it.widgetId }.joinToString { "," }}")
            Log.d("HomeScreen", "activeWidgetList: ${activeWidgetList.joinToString { "," }}")
            uiState.list.forEach {
                if (!activeWidgetList.contains(it.widgetId)) {
                    repository.delete(it.widgetId)
                    Log.d("HomeScreen", "cleanup widget ${it.widgetId} which isn't active.")
                }
            }
            delay(100)
            onComplete.invoke()
        }
    }

    private fun getActiveWidgetList(): List<Int> {
        val appWidgetManager = AppWidgetManager.getInstance(appContext)
        val provider = ComponentName(appContext, SteamPriceWidgetProvider::class.java)

        val currentWidgetIds = appWidgetManager.getAppWidgetIds(provider)
        return currentWidgetIds.toList()
    }
}