package com.mikeapp.steamob.ui.addwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.mikeapp.steamob.ui.commonui.SystemUiUtil
import com.mikeapp.steamob.ui.theme.SteamObTheme

class AddWidgetInputActivity : ComponentActivity() {
    private var forceDark = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val isNewWidget = intent.action == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE

        setContent {
            SteamObTheme {
                Scaffold {
                    AddWidgetNav(
                        appWidgetId = appWidgetId,
                        isNewWidget = isNewWidget,
                        forceDark = {
                            forceDark = it
                            SystemUiUtil.applySystemBarStyle(this@AddWidgetInputActivity, forceDark)
                        },
                        onFinish = { result, resultIntent ->
                            setResult(result, resultIntent)
                            finish()
                        },
                        onDismiss = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}