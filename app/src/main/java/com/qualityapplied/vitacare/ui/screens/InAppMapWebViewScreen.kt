package com.qualityapplied.vitacare.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Handles custom URL schemes (intent://, geo:, market://) gracefully to prevent net::ERR_UNKNOWN_URL_SCHEME.
 */
private fun handleUrlLoading(context: Context, view: WebView?, urlStr: String): Boolean {
    if (urlStr.startsWith("http://") || urlStr.startsWith("https://")) {
        // Block Google Maps app chooser redirects so map stays embedded inside WebView
        if (urlStr.contains("maps.app.goo.gl") || urlStr.contains("app=gmm")) {
            return true
        }
        return false // Allow WebView to load standard web link
    }
    // Handle custom intent://, geo:, market:// or non-HTTP schemes
    try {
        val intent = Intent.parseUri(urlStr, Intent.URI_INTENT_SCHEME)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return true
        }
    } catch (_: Exception) {
    }
    return true
}

/**
 * In-App WebView Navigation Screen for displaying map routes & directions inside Suwago.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppMapWebViewScreen(
    title: String,
    embedUrl: String,
    externalUrl: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize()) {
        // TopAppBar styled with Suwago theme color (#0F766E) with External Maps Browser Button
        TopAppBar(
            title = {
                Text(
                    text = title.ifBlank { "Map Navigation" },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            },
            actions = {
                // Button to open in Google Maps App or External Browser
                IconButton(onClick = {
                    try {
                        val uri = Uri.parse(externalUrl.ifBlank { embedUrl })
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                    }
                }) {
                    Text("🌐", fontSize = 18.sp)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF0F766E), // Suwago theme primary teal
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        )

        val htmlContent = remember(embedUrl) {
            """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        html, body { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background-color: #f8fafc; }
                        iframe { width: 100%; height: 100%; border: 0; }
                    </style>
                </head>
                <body>
                    <iframe src="$embedUrl" allowfullscreen></iframe>
                </body>
                </html>
            """.trimIndent()
        }

        // WebView displaying Google Maps directions inside iframe in-app
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setGeolocationEnabled(true)
                    settings.setJavaScriptCanOpenWindowsAutomatically(true)

                    webChromeClient = object : WebChromeClient() {
                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String?,
                            callback: GeolocationPermissions.Callback?,
                        ) {
                            callback?.invoke(origin, true, false)
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        @Deprecated("Deprecated in Java")
                        override fun shouldOverrideUrlLoading(view: WebView?, urlStr: String?): Boolean {
                            if (urlStr == null) return false
                            return handleUrlLoading(ctx, view, urlStr)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?,
                        ): Boolean {
                            val urlStr = request?.url?.toString() ?: return false
                            return handleUrlLoading(ctx, view, urlStr)
                        }
                    }
                    loadDataWithBaseURL("https://www.google.com", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://www.google.com", htmlContent, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
