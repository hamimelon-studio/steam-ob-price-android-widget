package com.mike.steamob.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.compose.ui.tooling.data.EmptyGroup.data
import com.mike.steamob.AddWidgetInputActivity
import com.mike.steamob.R
import com.mike.steamob.alarm.NotificationLauncher
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.data.room.SteamObEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SteamPriceWidgetProvider : AppWidgetProvider() {
    private val repository: SteamPriceRepository = get(SteamPriceRepository::class.java)

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
            val widgetState = appId?.let {
                repository.fetchApp(appId)
            }
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            with(views) {
                showAlertWhen(widgetState == null)
                showRedBackgroundOnMajorDiscount(widgetState?.discountLevel)
                widgetState?.let {
                    displayInfo(it)
                }
                setupRefreshIntent(context, appWidgetId)
                setupConfigIntent(context, appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)

            widgetState?.let { triggerAlarm(context, appWidgetId, widgetState) }
        }
    }

    private fun SteamObEntity.toWidgetState(): WidgetState {
        return WidgetState(
            timeUpdated = this.lastUpdate.toString(),
            name = appName,
            discount = "$discount% OFF",
            discountLevel = mapDiscountLevel(discount),
            initialPrice = formatAud(rrp),
            price = formatAud(finalPrice)
        )
    }

    private fun triggerAlarm(context: Context, appWidgetId: Int, widgetState: WidgetState) {
        val priceThreshold = getPriceThreshold(context, appWidgetId)
        val finalPrice = extractPrice(widgetState.price)
        if (finalPrice <= priceThreshold) {
            val title = "\uD83D\uDD25 [SteamOb] ${widgetState.name}: Big Discount Just for You!"
            val message =
                "\"The price of ${widgetState.name} has dropped to just ${widgetState.price}! Don’t miss out on this limited-time offer – grab it before it’s gone! \uD83D\uDCA5\""
            NotificationLauncher.post(context, title, message)
        }
        widgetState.price
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

    private fun RemoteViews.displayInfo(widgetState: WidgetState) {
        setTextViewText(R.id.time, "on ${widgetState.timeUpdated}")
        setTextViewText(R.id.name, widgetState.name)
        setTextViewText(R.id.discount, widgetState.discount)
        val priceDisplay = "${widgetState.price} (${widgetState.initialPrice})"
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

    private fun getCurrentTimeStampString(): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val currentTime: String = dateFormat.format(Date())
        return currentTime
    }

    private fun formatAud(price: Long): String = String.format("A\$ ${price / 100.0f}")

    private fun mapDiscountLevel(discountPercentage: Int): DiscountLevel =
        when {
            discountPercentage == 0 -> DiscountLevel.None
            discountPercentage < 50 -> DiscountLevel.Minor
            else -> DiscountLevel.Major
        }

    companion object {
        private const val ACTION_REFRESH = "action-refresh"
    }
}