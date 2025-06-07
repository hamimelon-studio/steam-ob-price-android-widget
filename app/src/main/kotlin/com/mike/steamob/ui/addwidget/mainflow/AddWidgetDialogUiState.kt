package com.mike.steamob.ui.addwidget.mainflow

sealed class AddWidgetDialogUiState {
    data object Loading : AddWidgetDialogUiState()

    data class DialogReady(
        val widgetId: Int,
        val appId: String,
        val threshold: Long
    ) : AddWidgetDialogUiState()

    data class Debug(
        val widgetId: Int,
        val action: String?,
        val delay: Long
    ) : AddWidgetDialogUiState()
}