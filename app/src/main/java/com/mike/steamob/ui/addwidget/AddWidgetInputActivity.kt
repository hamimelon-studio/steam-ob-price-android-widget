package com.mike.steamob.ui.addwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.mike.steamob.ui.commonui.SystemUiUtil
import com.mike.steamob.ui.theme.SteamObTheme

class AddWidgetInputActivity : ComponentActivity() {
    private var forceDark = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        enableEdgeToEdge()

        setContent {
            SteamObTheme {
                Scaffold {
                    AddWidgetNav(
                        appWidgetId = appWidgetId,
                        forceDark = {
                            forceDark = it
                            SystemUiUtil.applySystemBarStyle(this@AddWidgetInputActivity, forceDark)
                        }
                    ) { result, resultIntent ->
                        setResult(result, resultIntent)
                        finish()
                    }
                }
            }
        }
    }
}