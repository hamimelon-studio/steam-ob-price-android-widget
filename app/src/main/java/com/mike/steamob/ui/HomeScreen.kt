package com.mike.steamob.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel: HomeViewModel = koinViewModel()

    PullToRefreshBox(
        isRefreshing = viewModel.isRefresh.collectAsState().value,
        onRefresh = { viewModel.forceRefresh() },
        modifier = Modifier.padding(innerPadding)
    ) {
        viewModel.uiState.collectAsState().value?.run {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                list.forEach { appInfo ->
                    Row {
                        Text(appInfo.appId)
                    }
                }
            }
        } ?: EmptyScreen(innerPadding)
    }
}

@Composable
fun EmptyScreen(innerPadding: PaddingValues) {
    Box(modifier = Modifier.padding(innerPadding)) {
        Text("No apps added. Please add from home screen widget.")
    }
}