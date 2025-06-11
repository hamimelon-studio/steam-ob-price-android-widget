package com.mike.steamob.release.ui.addwidget

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.mike.steamob.release.ui.addwidget.mainflow.AddWidgetNav
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddWidgetDispatchScreen(
    widgetId: Int,
    intentAction: String?,
    forceDark: (Boolean) -> Unit,
    onFinish: () -> Unit,
    onFinishWithResult: (Int, Intent) -> Unit,
) {
    val viewModel: AddWidgetViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState()

    Log.d("Lifecycle", "Input.onCreate() intent.action: $intentAction")
    Log.d("Lifecycle", "Input.onCreate() delay, appWidgetId: $widgetId")

    LaunchedEffect(widgetId, intentAction) {
        viewModel.load(widgetId, intentAction) {
            onFinish.invoke()
        }
    }

    when (uiState.value) {
        is AddWidgetUiState.Idle -> IdleScreen()

        is AddWidgetUiState.Debug -> DebugScreen(uiState.value as AddWidgetUiState.Debug)

        is AddWidgetUiState.InputDialog -> (uiState.value as? AddWidgetUiState.InputDialog)?.let {
            AddWidgetNav(
                appWidgetId = it.widgetId,
                isNewEntry = it.isStartFromIntro,
                forceDark = { flag ->
                    forceDark.invoke(flag)
                },
                onFinish = { result, resultIntent ->
                    onFinishWithResult.invoke(result, resultIntent)
                },
                onDismiss = {
                    onFinish.invoke()
                }
            )
        } ?: IdleScreen()
    }
}