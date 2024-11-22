package com.mike.steamob.ui.home

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SteamWebScreen(appId: String, innerPadding: PaddingValues) {
    val url = "https://steamdb.info/app/$appId/"
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