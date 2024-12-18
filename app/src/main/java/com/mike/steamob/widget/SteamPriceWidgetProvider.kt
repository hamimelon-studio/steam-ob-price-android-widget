package com.mike.steamob.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.mike.steamob.AddWidgetInputActivity
import com.mike.steamob.R
import com.mike.steamob.alarm.NotificationLauncher
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.data.room.SteamObEntity
import com.mike.steamob.ui.DisplayMapper.toDiscountDisplay
import com.mike.steamob.ui.DisplayMapper.toPriceDisplay
import com.mike.steamob.ui.DisplayMapper.toWidgetDisplayTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

class SteamPriceWidgetProvider : AppWidgetProvider() {
    private val repository: SteamPriceRepository = get(SteamPriceRepository::class.java)

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.d("bbbb", "onEnabled")
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Log.d("bbbb", "onRestored")
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        Log.d("bbbb", "onDeleted")
        coroutineScope.launch(IO) {
            appWidgetIds?.forEach {
                repository.delete(it)
            }
        }

        super.onDeleted(context, appWidgetIds)
    }

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
        Log.d("bbbb", "widget onReceive: ${intent.action}")
        if (ACTION_REFRESH == intent.action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            Log.d("bbbb", "widget appWidgetId: $appWidgetId")
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
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            val entity = repository.getSteamObEntity(appWidgetId)
            Log.d("bbbb", "entity in db: $entity, appWidgetId: $appWidgetId")
            val entityOutput = entity?.let {
                repository.fetchApp(entity)
            }
            views.showAlertWhen(false)
            with(views) {
                views.showAlertWhen(entityOutput == null)
                if (entityOutput != null) {
                    showRedBackgroundOnMajorDiscount(entityOutput.finalPrice < entityOutput.alarmThreshold)
                    val widgetState = entityOutput.toWidgetState(context)
                    displayInfo(widgetState)
                }
                setupRefreshIntent(context, appWidgetId)
                setupConfigIntent(context, appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
            entityOutput?.let {
                triggerAlarm(context, entityOutput)
            }
        }
    }

    private fun SteamObEntity.toWidgetState(context: Context): WidgetUiState {
        return WidgetUiState(
            timeUpdated = toWidgetDisplayTime(context, lastUpdate),
            name = appName,
            discount = toDiscountDisplay(context, discount),
            price = toPriceDisplay(context, rrp, finalPrice)
        )
    }

    private fun triggerAlarm(context: Context, entity: SteamObEntity) {
        if (entity.finalPrice <= entity.alarmThreshold) {
            val priceDisplay =
                context.getString(R.string.currency_formater, entity.finalPrice / 100f)
            val title = context.getString(R.string.notification_title, entity.appName)
            val message =
                context.getString(R.string.notification_message, entity.appName, priceDisplay)
            NotificationLauncher.post(context, title, message)
        }
    }

    private fun RemoteViews.setupConfigIntent(context: Context, appWidgetId: Int) {
        val intent = getSetupConfigIntent(context, appWidgetId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        setOnClickPendingIntent(R.id.config_button, pendingIntent)
    }

    private fun getSetupConfigIntent(context: Context, appWidgetId: Int): Intent {
        val intent = Intent(context, AddWidgetInputActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        return intent
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

    private fun RemoteViews.displayInfo(widgetUiState: WidgetUiState) {
        setTextViewText(R.id.time, widgetUiState.timeUpdated)
        setTextViewText(R.id.name, widgetUiState.name)
        setTextViewText(R.id.discount, widgetUiState.discount)
        val priceDisplay = widgetUiState.price
        setTextViewText(R.id.price, priceDisplay)
    }

    private fun RemoteViews.showRedBackgroundOnMajorDiscount(alarm: Boolean) {
        if (alarm) {
            setImageViewResource(
                R.id.widget_background,
                R.drawable.steam_bg_red
            )
        } else {
            setImageViewResource(
                R.id.widget_background,
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