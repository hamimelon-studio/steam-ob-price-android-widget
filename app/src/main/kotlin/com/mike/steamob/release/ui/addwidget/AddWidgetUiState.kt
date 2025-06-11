package com.mike.steamob.release.ui.addwidget

sealed class AddWidgetUiState {
    data object Idle : AddWidgetUiState()

    data class InputDialog(
        val isStartFromIntro: Boolean,
        val widgetId: Int,
        val appId: String,
        val threshold: Long
    ) : AddWidgetUiState()

    data class Debug(
        val widgetId: Int,
        val action: String?,
        val delay: Long
    ) : AddWidgetUiState()
}