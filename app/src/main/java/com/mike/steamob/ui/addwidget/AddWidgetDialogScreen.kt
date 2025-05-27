package com.mike.steamob.ui.addwidget

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.mike.steamob.data.room.SteamObEntity
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddWidgetDialogScreen(
    appWidgetId: Int,
    steamAppId: String?,
    onBack: () -> Unit,
    onFinish: (Int, Intent) -> Unit
) {
    val viewModel: AddWidgetViewModel = koinViewModel()
    var showInputDialog by remember { mutableStateOf(true) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var entity: SteamObEntity? by remember { mutableStateOf(null) }

    LaunchedEffect(appWidgetId) {
        entity = viewModel.loadEntity(appWidgetId)
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
                onFinish.invoke(result, resultIntent)
            }
        }) {
            AddWidgetErrorDialog {
                viewModel.finishWithResult(appWidgetId, false) { result, resultIntent ->
                    onFinish.invoke(result, resultIntent)
                }
                showErrorDialog = false
            }
        }
    }

    if (showInputDialog && !isLoading) {
        AddWidgetDialog(
            appId0 = entity?.appId ?: steamAppId ?: "",
            threshold0 = entity?.alarmThreshold?.let { it / 100f } ?: 0f,
            onDismiss = { onBack.invoke() },
            onConfirm = { appId, priceThreshold ->
                isLoading = true
                viewModel.save(appWidgetId, appId, priceThreshold) { success ->
                    isLoading = false
                    viewModel.updateWidget(appWidgetId)
                    if (success) {
                        viewModel.finishWithResult(appWidgetId, true) { result, resultIntent ->
                            onFinish.invoke(result, resultIntent)
                        }
                    } else {
                        showInputDialog = false
                        showErrorDialog = true
                        onBack.invoke()
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