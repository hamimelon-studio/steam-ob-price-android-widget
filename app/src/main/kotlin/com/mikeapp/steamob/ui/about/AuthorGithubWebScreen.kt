package com.mikeapp.steamob.ui.about

import android.webkit.WebView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AuthorGithubWebScreen(url: String, scrollY: Int, innerPadding: PaddingValues) {
    AndroidView(
        modifier = Modifier.padding(innerPadding),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                loadUrl(url)
            }
        },
        update = { webView ->
            // Scroll to the desired position after the content is loaded
            webView.post {
                webView.scrollTo(0, scrollY)
            }
        }
    )
}