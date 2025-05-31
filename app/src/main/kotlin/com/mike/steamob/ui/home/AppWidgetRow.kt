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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mike.steamob.R
import com.mike.steamob.data.room.SteamObEntity

@Composable
fun AppWidgetRow(
    appInfo: SteamObEntity,
    modifier: Modifier = Modifier,
    onGearClick: (Int) -> Unit,
    onOpenLink: (String) -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.primaryContainer

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(appInfo.logo)
                .crossfade(true)
                .build(),
            contentDescription = "Game Logo",
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.FillHeight              // fills bounds nicely
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = appInfo.appName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = contentColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(onClick = {
            onOpenLink.invoke(appInfo.appId)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_open_in_new_24),
                contentDescription = "Info",
                tint = contentColor
            )
        }
        IconButton(onClick = {
            onGearClick.invoke(appInfo.widgetId)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings_24),
                contentDescription = "Settings",
                tint = contentColor
            )
        }
    }
}