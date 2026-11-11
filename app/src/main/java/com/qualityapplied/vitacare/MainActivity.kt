package com.qualityapplied.vitacare

import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.qualityapplied.vitacare.data.LocaleManager
import com.qualityapplied.vitacare.ui.AppRoot
import com.qualityapplied.vitacare.ui.theme.VitaCareTheme

// AppCompatActivity is required so edge-to-edge and system integration work cleanly across Android versions.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)

        // Initialize saved language state for instant Jetpack Compose recomposition
        LocaleManager.init(this)

        // Disable OS dark scrim so yellow-green gradient displays clearly behind back/home buttons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            val context = LocalContext.current
            val currentLocale = LocaleManager.currentLocale

            val localizedContext = remember(currentLocale) {
                LocaleManager.getLocalizedContext(context, currentLocale)
            }
            val localizedConfig = remember(currentLocale) {
                localizedContext.resources.configuration
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfig,
                LocalActivityResultRegistryOwner provides this@MainActivity,
                LocalOnBackPressedDispatcherOwner provides this@MainActivity,
            ) {
                VitaCareTheme {
                    AppRoot()
                }
            }
        }
    }
}
