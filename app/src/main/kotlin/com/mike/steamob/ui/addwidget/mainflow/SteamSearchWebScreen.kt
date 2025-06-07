package com.mike.steamob.ui.addwidget.mainflow

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SteamSearchWebScreen(
    keyword: String,
    isFromHomeScreen: Boolean,
    innerPadding: PaddingValues,
    onProceed: (String?) -> Unit
) {
    val searchUrl = "https://steamdb.info/search/?a=all&q=${keyword}"
    var currentUrl by remember { mutableStateOf(searchUrl) }
    var webView: WebView? by remember { mutableStateOf(null) }
    val clipboardManager = LocalClipboardManager.current

    // Regex to match app ID from URL
    val appIdRegex = Regex("""steamdb\.info/app/(\d+)/?""")
    val appId = appIdRegex.find(currentUrl)?.groupValues?.get(1)

    // Handle back navigation within WebView
    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            return false // Stay in WebView
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            url?.let { currentUrl = it }
                        }
                    }
                    loadUrl(searchUrl)
                    webView = this
                }
            },
            update = {
                webView = it
                currentUrl = it.url ?: searchUrl
            }
        )

        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = {
                if (!isFromHomeScreen && appId != null) {
                    clipboardManager.setText(AnnotatedString(appId))
                }
                webView?.let {
                    onProceed.invoke(appId)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                appId?.let {
                    if (!isFromHomeScreen) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = appId,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    if (isFromHomeScreen) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Select"
                        )
                    }
                } ?: Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = "Select"
                )
            }
        }
    }
}
