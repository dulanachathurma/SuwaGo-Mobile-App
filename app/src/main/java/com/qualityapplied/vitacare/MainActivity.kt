package com.qualityapplied.vitacare

import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.qualityapplied.vitacare.ui.AppRoot
import com.qualityapplied.vitacare.ui.theme.VitaCareTheme

// AppCompatActivity is required so the in-app language switch (EN / SI / TA) works on every Android version.
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

        // Disable OS dark scrim so yellow-green gradient displays clearly behind back/home buttons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            VitaCareTheme {
                AppRoot()
            }
        }
    }
}