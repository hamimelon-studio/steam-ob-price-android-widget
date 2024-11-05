package com.mike.steamob.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.mike.steamob.AddWidgetInputActivity
import com.mike.steamob.R
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.ui.DiscountLevel
import com.mike.steamob.ui.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SteamPriceWidgetProvider : AppWidgetProvider() {
    private val repository = SteamPriceRepository()

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetId, appWidgetManager)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_REFRESH == intent.action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val appWidgetManager = AppWidgetManager.getInstance(context)
            updateWidget(context, appWidgetId, appWidgetManager)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetId: Int,
        appWidgetManager: AppWidgetManager
    ) {
        coroutineScope.launch {
            val appId = getAppId(context, appWidgetId)
            val uiState = appId?.let {
                repository.fetchData(appId)
            }
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            with(views) {
                showAlertWhen(uiState == null)
                hideFinalPriceIfNoDiscount(uiState?.discountLevel)
                showRedBackgroundOnMajorDiscount(uiState?.discountLevel)
                uiState?.let {
                    displayInfo(it)
                }
                setupRefreshIntent(context, appWidgetId)
                setupConfigIntent(context, appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getAppId(context: Context, appWidgetId: Int): String? {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val key = "widget_id_$appWidgetId"
        return prefs.getString(key, null)
    }

    private fun RemoteViews.setupConfigIntent(context: Context, appWidgetId: Int) {
        val intent = Intent(context, AddWidgetInputActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        setOnClickPendingIntent(R.id.config_button, pendingIntent)
    }

    private fun RemoteViews.setupRefreshIntent(context: Context, appWidgetId: Int) {
        val refreshIntent = Intent(context, SteamPriceWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setOnClickPendingIntent(R.id.refresh_button, pendingIntent)
    }

    private fun RemoteViews.displayInfo(uiState: UiState) {
        setTextViewText(R.id.time, "on ${uiState.timeUpdated}")
        setTextViewText(R.id.name, uiState.name)
        setTextViewText(R.id.discount, uiState.discount)
        setTextViewText(R.id.initialPrice, uiState.initialPrice)
        setTextViewText(R.id.price, uiState.price)
    }

    private fun RemoteViews.showRedBackgroundOnMajorDiscount(discountLevel: DiscountLevel?) {
        if (discountLevel == DiscountLevel.Major) {
            setInt(
                R.id.widget_background,
                "setBackgroundResource",
                R.drawable.steam_bg_red
            )
        } else {
            setInt(
                R.id.widget_background,
                "setBackgroundResource",
                R.drawable.steam_bg
            )
        }
    }

    private fun RemoteViews.hideFinalPriceIfNoDiscount(discountLevel: DiscountLevel?) {
        if (discountLevel == DiscountLevel.None) {
            setViewVisibility(R.id.price, View.GONE)
        } else {
            setViewVisibility(R.id.price, View.VISIBLE)
        }
    }

    private fun RemoteViews.showAlertWhen(alertOn: Boolean) {
        if (alertOn) {
            setViewVisibility(R.id.alert_icon, View.VISIBLE)
        } else {
            setViewVisibility(R.id.alert_icon, View.GONE)
        }
    }

    companion object {
        private const val ACTION_REFRESH = "action-refresh"
    }
}