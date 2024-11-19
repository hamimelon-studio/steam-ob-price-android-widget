package com.mike.steamob.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mike.steamob.data.SteamPriceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: SteamPriceRepository) : ViewModel() {
    val isRefresh = MutableStateFlow(false)

    val uiState = MutableStateFlow<UiState?>(null)

    init {
        forceRefresh()
    }

    fun forceRefresh() {
        viewModelScope.launch {
            try {
                isRefresh.value = true
                uiState.value = repository.fetchAllApps()
            } catch (e: Exception) {
                Log.d("bbbb", "Exception: $e")
                e.printStackTrace()
                uiState.value = null
            } finally {
                withContext(Dispatchers.Main) {
                    isRefresh.value = false
                }
            }
        }
    }
}