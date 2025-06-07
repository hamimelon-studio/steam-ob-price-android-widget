package com.mike.steamob.ui.addwidget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DebugScreen(uiState: AddWidgetUiState.Debug) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Text("widgetId: ${uiState.widgetId}")
        Text("action: ${uiState.action}")
        Text("delay: ${uiState.delay}")
    }
}
