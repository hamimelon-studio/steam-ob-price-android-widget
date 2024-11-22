package com.mike.steamob.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
            scope.launch {
                viewModel.forceRefresh()
            }
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
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
                    EmptyScreen()
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
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun GamesList(
    navController: NavController,
    games: List<SteamObEntity>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = games,
            key = { it.appId }
        ) { appInfo ->
            GameCard(navController, appInfo)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameCard(
    navController: NavController,
    appInfo: SteamObEntity
) {
    val cardShape = RoundedCornerShape(4.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = cardShape
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        onClick = { /* Handle click */ }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val primaryColor = MaterialTheme.colorScheme.primary

            // Left border accent
            Canvas(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.8f),
                            primaryColor.copy(alpha = 0.2f)
                        )
                    )
                )
            }

            AppWidgetRow(
                navController = navController,
                appInfo = appInfo,
                modifier = Modifier.padding(start = 4.dp) // Offset for border
            )
        }
    }
}