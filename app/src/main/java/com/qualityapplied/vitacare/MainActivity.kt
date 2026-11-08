package com.qualityapplied.vitacare

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.qualityapplied.vitacare.ui.AppRoot
import com.qualityapplied.vitacare.ui.theme.VitaCareTheme

// AppCompatActivity is required so the in-app language switch (EN / SI / TA) works on every Android version.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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
