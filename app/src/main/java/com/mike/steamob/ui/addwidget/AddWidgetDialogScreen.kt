package com.mike.steamob.ui.addwidget

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddWidgetDialogScreen(
    appWidgetId: Int,
    steamAppId: String?,
    onBack: () -> Unit,
    onFinish: (Int, Intent) -> Unit
) {
    val viewModel: AddWidgetViewModel = koinViewModel()
    val steamObEntity by viewModel.steamObEntity.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showInputDialog by remember { mutableStateOf(true) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appWidgetId) {
        viewModel.load(appWidgetId)
    }

    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator()
        }
    }

    if (showErrorDialog && !isLoading) {
        Dialog(onDismissRequest = {
            showErrorDialog = false
            viewModel.finishWithResult(appWidgetId, false) { result, resultIntent ->
                onFinish(result, resultIntent)
            }
        }) {
            AddWidgetErrorDialog {
                viewModel.finishWithResult(appWidgetId, false) { result, resultIntent ->
                    onFinish(result, resultIntent)
                }
                showErrorDialog = false
            }
        }
    }

    if (showInputDialog && !isLoading) {
        AddWidgetDialog(
            appId0 = steamObEntity?.appId ?: steamAppId ?: "",
            threshold0 = steamObEntity?.alarmThreshold?.let { it / 100f } ?: 0f,
            onDismiss = {
                showInputDialog = false
                onBack()
            },
            onConfirm = { appId, priceThreshold ->
                viewModel.save(appWidgetId, appId, priceThreshold) { success ->
                    viewModel.updateWidget(appWidgetId)
                    if (success) {
                        viewModel.finishWithResult(appWidgetId, true) { result, resultIntent ->
                            onFinish(result, resultIntent)
                        }
                    } else {
                        showInputDialog = false
                        showErrorDialog = true
                        onBack()
                    }
                }
            }
        )
    }

    // Optional: transparent box to reserve space or intercept touch
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    )
}