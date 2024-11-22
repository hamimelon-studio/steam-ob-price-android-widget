package com.mike.steamob

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.mike.steamob.data.SteamPriceRepository
import com.mike.steamob.data.room.SteamObEntity
import com.mike.steamob.ui.addwidget.AddWidgetDialog
import com.mike.steamob.ui.addwidget.AddWidgetErrorDialog
import com.mike.steamob.ui.theme.SteamObTheme
import com.mike.steamob.widget.SteamPriceWidgetProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get

class AddWidgetInputActivity : ComponentActivity() {
    private val repository: SteamPriceRepository = get(SteamPriceRepository::class.java)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        enableEdgeToEdge()
        val entity = runBlocking {
            withContext(IO) {
                repository.getSteamObEntity(appWidgetId)
            }
        }

        setContent {
            SteamObTheme {
                Scaffold {
                    var showInputDialog by remember { mutableStateOf(true) }
                    var showErrorDialog by remember { mutableStateOf(false) }
                    var isLoading by remember { mutableStateOf(false) }

                    if (isLoading) {
                        Dialog(onDismissRequest = {}) {
                            CircularProgressIndicator()
                        }
                    }

                    if (showErrorDialog && !isLoading) {
                        Dialog(onDismissRequest = {
                            showErrorDialog = false
                            finishWithResult(appWidgetId, false)
                        }) {
                            AddWidgetErrorDialog {
                                finishWithResult(appWidgetId, false)
                                showErrorDialog = false
                            }
                        }
                    }

                    if (showInputDialog && !isLoading) {
                        AddWidgetDialog(
                            appId0 = entity?.appId ?: "",
                            threshold0 = entity?.alarmThreshold?.let { it / 100f } ?: 0f,
                            onDismiss = { finish() },
                            onConfirm = { appId, priceThreshold ->
                                isLoading = true
                                save(appWidgetId, appId, priceThreshold) { success ->
                                    isLoading = false
                                    updateWidget(appWidgetId)
                                    if (success) {
                                        finishWithResult(appWidgetId, success)
                                    } else {
                                        showInputDialog = false
                                        showErrorDialog = true
                                    }
                                }
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }

    private fun updateWidget(appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        // Create an Intent to update the widget
        val intent = Intent(this, SteamPriceWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        // Update the widget
        appWidgetManager.updateAppWidget(
            appWidgetId,
            RemoteViews(packageName, R.layout.widget_layout)
        )

        // Alternatively, you could call your specific update method:
        SteamPriceWidgetProvider().onUpdate(this, appWidgetManager, intArrayOf(appWidgetId))
    }

    private fun save(
        appWidgetId: Int, appId: String, priceThreshold: Float,
        onComplete: (Boolean) -> Unit
    ) {
        lifecycleScope.launch(IO) {
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

    private fun finishWithResult(appWidgetId: Int, success: Boolean) {
        val result = if (success) RESULT_OK else RESULT_CANCELED
        val resultIntent = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(result, resultIntent)
        finish()
    }
}