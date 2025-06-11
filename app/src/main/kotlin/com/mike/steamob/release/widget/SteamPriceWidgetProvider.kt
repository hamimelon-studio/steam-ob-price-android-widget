package com.mike.steamob.release.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.mike.steamob.release.R
import com.mike.steamob.release.alarm.NotificationLauncher
import com.mike.steamob.release.data.room.SteamAppState
import com.mike.steamob.release.data.room.SteamObEntity
import com.mike.steamob.release.ui.DisplayMapper.toDiscountDisplay
import com.mike.steamob.release.ui.DisplayMapper.toPriceDisplay
import com.mike.steamob.release.ui.DisplayMapper.toWidgetDisplayTime
import com.mike.steamob.release.ui.theme.WidgetGreen
import com.mike.steamob.release.ui.theme.WidgetGreenHighlight
import com.mike.steamob.release.ui.theme.WidgetGrey
import com.mike.steamob.release.ui.theme.WidgetGreyHighlight
import com.mike.steamob.release.ui.theme.WidgetRed
import com.mike.steamob.release.ui.theme.WidgetRedHighlight
import com.mike.steamob.release.ui.theme.WidgetYellow
import com.mike.steamob.release.ui.theme.WidgetYellowHighlight
import com.mike.steamob.release.ui.util.InternationaliseUtil.formatCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.java.KoinJavaComponent.get

class SteamPriceWidgetProvider : AppWidgetProvider() {
    private val useCase: SteamPriceWidgetProviderUseCase = get(SteamPriceWidgetProviderUseCase::class.java)

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        useCase.deleteWidgets(appWidgetIds?.toList() ?: emptyList())
        super.onDeleted(context, appWidgetIds)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("Lifecycle", "onUpdate, appWidgetIds: ${appWidgetIds.joinToString(",")}")
        for (appWidgetId in appWidgetIds) {
            useCase.saveWidgetIfNewCreated(appWidgetId)
            updateWidget(context, appWidgetId, appWidgetManager)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(
            "Lifecycle", "onReceive, intent.action: ${intent.action}, widgetId:${
                intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }"
        )
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
        useCase.asyncFetchEntityByWidgetId(appWidgetId) { entityOutput ->
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.showAlertWhen(false)
            with(views) {
                views.showAlertWhen(entityOutput == null)
                if (entityOutput != null) {
                    if (entityOutput.state == SteamAppState.NotAvailable.displayName) {
                        showGreyBackgroundWhenUnavailable()
                    } else {
                        showRedBackgroundOnMajorDiscount(entityOutput.finalPrice < entityOutput.alarmThreshold)
                    }
                    val widgetState = entityOutput.toWidgetState(context)
                    displayInfo(widgetState)
                }
                setupRefreshIntent(context, appWidgetId)
                setupConfigIntent(context, appWidgetId)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
            entityOutput?.let {
                if (entityOutput.state == SteamAppState.Normal.displayName) {
                    triggerAlarm(context, entityOutput)
                }
            }
        }
    }

    private fun SteamObEntity.toWidgetState(context: Context): WidgetUiState {
        return WidgetUiState(
            timeUpdated = toWidgetDisplayTime(context, lastUpdate),
            name = appName,
            discount = when (state) {
                SteamAppState.Normal.displayName -> toDiscountDisplay(context, discount)
                else -> state
            },
            price = toPriceDisplay(rrp, finalPrice),
            state = SteamAppState.getStateFromName(state),
            isAlarm = state == SteamAppState.Normal.displayName && finalPrice <= alarmThreshold
        )
    }

    private fun triggerAlarm(context: Context, entity: SteamObEntity) {
        if (entity.finalPrice <= entity.alarmThreshold) {
            val priceDisplay = formatCurrency(entity.finalPrice)
            val title = context.getString(R.string.notification_title, entity.appName)
            val message =
                context.getString(R.string.notification_message, entity.appName, priceDisplay)
            val notificationId = entity.widgetId
            NotificationLauncher.post(context, title, message, notificationId)
        }
    }

    private fun RemoteViews.setupConfigIntent(context: Context, appWidgetId: Int) {
        val intent = useCase.getSetupConfigIntent(context, appWidgetId)
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

    private fun RemoteViews.displayInfo(widgetUiState: WidgetUiState) {
        setTextViewText(R.id.time, widgetUiState.timeUpdated)
        setTextViewText(R.id.name, widgetUiState.name)
        setTextViewText(R.id.discount, widgetUiState.discount)
        if (widgetUiState.state == SteamAppState.Normal) {
            setViewVisibility(R.id.price, View.VISIBLE)
            val priceDisplay = widgetUiState.price
            setTextViewText(R.id.price, priceDisplay)
        } else {
            setViewVisibility(R.id.price, View.GONE)
        }
        when (widgetUiState.state) {
            SteamAppState.Normal -> {
                if (widgetUiState.isAlarm) {
                    setInt(R.id.discount, "setBackgroundColor", WidgetRed)
                    setTextColor(R.id.discount, WidgetRedHighlight)
                } else {
                    setInt(R.id.discount, "setBackgroundColor", WidgetGreen)
                    setTextColor(R.id.discount, WidgetGreenHighlight)
                }
            }

            SteamAppState.ComingSoon -> {
                setInt(R.id.discount, "setBackgroundColor", WidgetYellow)
                setTextColor(R.id.discount, WidgetYellowHighlight)
            }

            SteamAppState.NotAvailable -> {
                setInt(R.id.discount, "setBackgroundColor", WidgetGrey)
                setTextColor(R.id.discount, WidgetGreyHighlight)
            }
        }
    }

    private fun RemoteViews.showRedBackgroundOnMajorDiscount(alarm: Boolean) {
        if (alarm) {
            setImageViewResource(
                R.id.widget_background,
                R.drawable.bg_red
            )
        } else {
            setImageViewResource(
                R.id.widget_background,
                R.drawable.bg_blue
            )
        }
    }

    private fun RemoteViews.showGreyBackgroundWhenUnavailable() {
        setImageViewResource(
            R.id.widget_background,
            R.drawable.bg_grey
        )
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