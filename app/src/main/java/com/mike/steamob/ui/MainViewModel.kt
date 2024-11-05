package com.mike.steamob.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mike.steamob.data.SteamPriceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
//    private val repository = SteamPriceRepository()
//
//    val uiState = MutableStateFlow<UiState?>(null)
//
//    fun fetchData() {
//        viewModelScope.launch {
//            try {
//                uiState.value = repository.fetchData()
//                Log.d("bbbb", "uiState.value: ${uiState.value}")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                uiState.value = null
//            }
//        }
//    }
}