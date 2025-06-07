package com.mike.steamob.ui.addwidget.mainflow

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val viewModel: AddWidgetDialogViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState(AddWidgetDialogUiState.Loading)

    LaunchedEffect(appWidgetId) {
        viewModel.load(appWidgetId)
    }

    if (uiState is AddWidgetDialogUiState.Loading) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator()
        }
    }

    if (uiState is AddWidgetDialogUiState.DialogReady) {
        val uiStateCast = uiState as AddWidgetDialogUiState.DialogReady
        AddWidgetDialog(
            appId0 = steamAppId ?: uiStateCast.appId,
            threshold0 = uiStateCast.threshold / 100f,
            onDismiss = {
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