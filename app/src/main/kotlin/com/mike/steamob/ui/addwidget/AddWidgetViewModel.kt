package com.mike.steamob.ui.addwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.ui.addwidget.CustomisedIntentConstant.ACTION_IN_APP_ADD_WIDGET_ENTRY
import com.mike.steamob.ui.addwidget.CustomisedIntentConstant.ACTION_IN_APP_TILE_CONFIG_ENTRY
import com.mike.steamob.ui.addwidget.CustomisedIntentConstant.ACTION_WIDGET_GEAR_CONFIG_ENTRY
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddWidgetViewModel(
    private val appContext: Context,
    private val repository: SteamPriceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddWidgetUiState>(AddWidgetUiState.Idle)
    val uiState: StateFlow<AddWidgetUiState> = _uiState

    fun load(appWidgetId: Int, intentAction: String?, onFinish: () -> Unit) {
        viewModelScope.launch(IO) {
            val localEntity = repository.getSteamObEntity(appWidgetId)
            Log.d("Lifecycle", "localEntity: $localEntity")

            // Added widget from inapp
            val isFromHomeScreenEntry = intentAction == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            Log.d("Lifecycle", "VM load appWidgetId: $appWidgetId, intentAction: $intentAction")
            _uiState.value =
                when {
                    isFromHomeScreenEntry -> AddWidgetUiState.InputDialog(
                        isStartFromIntro = true,
                        widgetId = localEntity?.widgetId ?: 0,
                        appId = localEntity?.appId ?: "",
                        threshold = localEntity?.alarmThreshold ?: 0
                    )

                    intentAction == ACTION_IN_APP_TILE_CONFIG_ENTRY -> {
                        AddWidgetUiState.InputDialog(
                            isStartFromIntro = true,
                            widgetId = localEntity?.widgetId ?: 0,
                            appId = localEntity?.appId ?: "",
                            threshold = localEntity?.alarmThreshold ?: 0
                        )
                    }

                    intentAction == ACTION_IN_APP_ADD_WIDGET_ENTRY -> {
                        val unconfiguredEntities =
                            repository.getAllEmptySteamObEntitiesByTimeStampDesc()
                        val lastOne = unconfiguredEntities.list.firstOrNull()
                        Log.d("Lifecycle", "add from inapp, lastOne: $lastOne")
                        lastOne?.let {
                            val delay = System.currentTimeMillis() - lastOne.lastUpdate
                            Log.d("Lifecycle", "add from inapp, delay: $delay")
                            if (delay > 2000) {
                                onFinish.invoke()
                                AddWidgetUiState.Idle
                            } else {
                                AddWidgetUiState.InputDialog(
                                    isStartFromIntro = true,
                                    widgetId = lastOne.widgetId,
                                    appId = "",
                                    threshold = 0
                                )
                            }
                        } ?: AddWidgetUiState.Idle
                    }

                    intentAction == ACTION_WIDGET_GEAR_CONFIG_ENTRY -> AddWidgetUiState.InputDialog(
                        isStartFromIntro = localEntity?.appId?.isEmpty() == true,
                        widgetId = localEntity?.widgetId ?: 0,
                        appId = localEntity?.appId ?: "",
                        threshold = localEntity?.alarmThreshold ?: 0
                    )

                    else -> AddWidgetUiState.InputDialog(
                        isStartFromIntro = false,
                        widgetId = localEntity?.widgetId ?: 0,
                        appId = localEntity?.appId ?: "",
                        threshold = localEntity?.alarmThreshold ?: 0
                    )
                }
        }
    }
}