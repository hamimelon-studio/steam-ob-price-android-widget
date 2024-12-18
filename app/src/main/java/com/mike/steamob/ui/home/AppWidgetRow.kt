package com.mike.steamob.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mike.steamob.R
import com.mike.steamob.data.room.SteamObEntity
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppWidgetRow(
    navController: NavController,
    appInfo: SteamObEntity,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = koinViewModel()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = appInfo.logo,
            contentDescription = "Game Logo",
            modifier = Modifier
                .height(32.dp)
                .clip(RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = appInfo.appName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate("steam/${appInfo.appId}")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info_outline_24),
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = {
                viewModel.launchAddWidgetInputActivity(appInfo.widgetId)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings_24),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}