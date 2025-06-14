package com.mike.steamob.release.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.mike.steamob.release.R
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel: HomeViewModel = koinViewModel()
    val isRefresh = viewModel.isRefresh.collectAsState().value
    val uiState = viewModel.uiState.collectAsState().value
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val applicationContext = LocalContext.current.applicationContext

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.forceRefresh()
                }

                else -> Unit
            }
        }

        // Add the observer
        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefresh,
        onRefresh = {
            scope.launch(IO) { viewModel.forceRefresh() }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 8.dp)
    ) {
        when (uiState?.list?.size) {
            null -> Unit

            0 -> EmptyScreen {
                viewModel.requestPinWidget(applicationContext)
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    HeaderSection()
                }
                items(
                    items = uiState.list,
                    key = { it.widgetId }
                ) { appInfo ->
                    if (appInfo.appId.isNotEmpty()) {
                        AppWidgetRow(
                            appInfo = appInfo,
                            onGearClick = {
                                viewModel.launchAddWidgetInputActivity(it, false)
                            },
                            onOpenLink = {
                                navController.navigate("steam/$it")
                            }
                        )
                    } else {
                        UnconfiguredSteamWidgetCard(appInfo.widgetId) {
                            viewModel.launchAddWidgetInputActivity(appInfo.widgetId, true)
                        }
                    }

                    HorizontalDivider()
                }
                item {
                    HowToAddWidgetCard {
                        viewModel.requestPinWidget(applicationContext)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Text(
        text = LocalContext.current.getString(R.string.widget_list_title),
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ),
        modifier = Modifier.padding(vertical = 8.dp),
    )
}