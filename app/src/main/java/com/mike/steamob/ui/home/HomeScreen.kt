package com.mike.steamob.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mike.steamob.R
import com.mike.steamob.data.room.SteamObEntity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel: HomeViewModel = koinViewModel()
    val isRefresh = viewModel.isRefresh.collectAsState().value
    val uiState = viewModel.uiState.collectAsState().value
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefresh,
        onRefresh = {
            scope.launch { viewModel.forceRefresh() }
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = uiState?.list?.isNotEmpty() == true || isRefresh,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) +
                            slideInVertically(animationSpec = tween(400)) with
                            fadeOut(animationSpec = tween(200))
                }, label = ""
            ) { hasContent ->
                if (!hasContent) {
                    EmptyScreen(navController)
                } else {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        HeaderSection()
                        Spacer(modifier = Modifier.height(8.dp))
                        GamesList(navController, uiState?.list ?: emptyList())
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = LocalContext.current.getString(R.string.widget_list_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp),
//                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun GamesList(
    navController: NavController,
    games: List<SteamObEntity>
) {
    val viewModel: HomeViewModel = koinViewModel()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = games,
            key = { it.appId }
        ) { appInfo ->
            GameCard {
                AppWidgetRow(
                    navController = navController,
                    viewModel = viewModel,
                    appInfo = appInfo,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        item {
            GameCard(
                backgroundColor = Color.Transparent
            ) {
                HowToAddWidgetCard {
                    navController.navigate("add")
                }
            }
        }
    }
}

