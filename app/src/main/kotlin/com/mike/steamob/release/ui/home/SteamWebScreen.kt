package com.mike.steamob.release.ui.home

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mike.steamob.release.ui.theme.GameExtremeDarkBlue

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SteamWebScreen(appId: String, innerPadding: PaddingValues) {
    val url = "https://steamdb.info/app/$appId/"
    Box(
        modifier = Modifier
            .background(color = GameExtremeDarkBlue)
    ) {
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            },
            update = { webView ->
                // Scroll to the desired position after the content is loaded
                webView.post {
                    webView.scrollTo(0, 2400)
                }
            }
        )
    }
}