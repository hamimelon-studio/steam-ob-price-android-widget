package com.mike.steamob.ui.addwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import com.mike.steamob.ui.commonui.SystemUiUtil.applySystemBarStyle
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

        setContent {
            SteamObTheme {
                Scaffold {
                    AddWidgetDispatchScreen(
                        widgetId = appWidgetId,
                        intentAction = intent.action,
                        forceDark = {
                            applySystemBarStyle(this@AddWidgetInputActivity, forceDark)
                        },
                        onFinish = {
                            finish()
                        },
                        onFinishWithResult = { result, intent ->
                            setResult(result, intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applySystemBarStyle(this@AddWidgetInputActivity, forceDark)
        }
    }
}