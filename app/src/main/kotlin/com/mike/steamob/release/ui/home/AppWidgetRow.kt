package com.mike.steamob.release.ui.home

import android.util.Log
import androidx.compose.foundation.Image
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mike.steamob.release.R
import com.mike.steamob.release.data.room.SteamObEntity

@Composable
fun AppWidgetRow(
    appInfo: SteamObEntity,
    modifier: Modifier = Modifier,
    onGearClick: (Int) -> Unit,
    onOpenLink: (String) -> Unit
) {
    Log.d("HomeScreen", "appInfo: $appInfo")
    val contentColor = MaterialTheme.colorScheme.primaryContainer
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(appInfo.logo)
            .crossfade(true)
            .build()
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (painter.state is AsyncImagePainter.State.Loading) {
            ShimmerEffect(
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
        Image(
            painter = painter,
            contentDescription = "Game Logo",
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.FillHeight
        )

        Spacer(Modifier.width(12.dp))
        Text(
            text = appInfo.appName,
            modifier = Modifier
                .weight(1f),
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