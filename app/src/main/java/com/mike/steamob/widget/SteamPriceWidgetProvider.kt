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
import com.mike.steamob.alarm.NotificationLauncher
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
                showRedBackgroundOnMajorDiscount(uiState?.discountLevel)
                uiState?.let {
                    displayInfo(it)
                }
                setupRefreshIntent(context, appWidgetId)
                setupConfigIntent(context, appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)

            uiState?.let { triggerAlarm(context, appWidgetId, uiState) }
        }
    }

    private fun triggerAlarm(context: Context, appWidgetId: Int, uiState: UiState) {
        val priceThreshold = getPriceThreshold(context, appWidgetId)
        val finalPrice = extractPrice(uiState.price)
        if (finalPrice <= priceThreshold) {
            val title = "\uD83D\uDD25 [SteamOb] ${uiState.name}: Big Discount Just for You!"
            val message =
                "\"The price of ${uiState.name} has dropped to just ${uiState.price}! Don’t miss out on this limited-time offer – grab it before it’s gone! \uD83D\uDCA5\""
            NotificationLauncher.post(context, title, message)
        }
        uiState.price
    }

    private fun extractPrice(priceString: String): Float {
        val priceWithoutSymbol = priceString.replace(Regex("^[^0-9.-]+"), "").trim()

        // Convert the cleaned string to a float
        return priceWithoutSymbol.toFloatOrNull() ?: 0f // Return 0f if conversion fails
    }

    private fun getAppId(context: Context, appWidgetId: Int): String? {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val key = "appid_$appWidgetId"
        return prefs.getString(key, null)
    }

    private fun getPriceThreshold(context: Context, appWidgetId: Int): Float {
        val sharedPreferences = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("threshold_$appWidgetId", 0f)
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
        val priceDisplay = "${uiState.price} (${uiState.initialPrice})"
        setTextViewText(R.id.price, priceDisplay)
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