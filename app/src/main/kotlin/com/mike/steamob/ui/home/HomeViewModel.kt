package com.mike.steamob.ui.home

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.ui.addwidget.AddWidgetInputActivity
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
        forceRefresh()
    }

    fun forceRefresh() {
        viewModelScope.launch(IO) {
            try {
                isRefresh.value = true
                uiState.value = repository.getSteamObEntities()
            } catch (e: Exception) {
                Log.d("bbbb", "Exception: $e")
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

    fun launchAddWidgetInputActivity(appWidgetId: Int) {
        val intent = getSetupConfigIntent(appContext, appWidgetId)
        appContext.startActivity(intent)
    }

    private fun getSetupConfigIntent(context: Context, appWidgetId: Int): Intent {
        val intent = Intent(context, AddWidgetInputActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return intent
    }
}