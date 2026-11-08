package com.qualityapplied.vitacare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Teal600 = Color(0xFF0D9488)
val Teal700 = Color(0xFF0F766E)
val Teal200 = Color(0xFF99F6E4)
val Teal100 = Color(0xFFCCFBF1)
val Teal50 = Color(0xFFF0FDFA)
val Emerald600 = Color(0xFF059669)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = Color(0xFFF8FAFC)
val Rose50 = Color(0xFFFFF1F2)
val Rose200 = Color(0xFFFECDD3)
val Rose500 = Color(0xFFF43F5E)
val Rose600 = Color(0xFFE11D48)
val Rose700 = Color(0xFFBE123C)
val Amber50 = Color(0xFFFFFBEB)
val Amber200 = Color(0xFFFDE68A)
val Amber600 = Color(0xFFD97706)
val Sky50 = Color(0xFFF0F9FF)
val Sky400 = Color(0xFF38BDF8)
val Sky600 = Color(0xFF0284C7)
val Emerald50 = Color(0xFFECFDF5)
val Emerald400 = Color(0xFF34D399)
val Amber400 = Color(0xFFFBBF24)
val Rose400 = Color(0xFFFB7185)

private val Scheme = lightColorScheme(
    primary = Teal600,
    onPrimary = Color.White,
    secondary = Emerald600,
    background = Slate50,
    surface = Color.White,
    onBackground = Slate800,
    onSurface = Slate800,
    error = Rose600,
)

@Composable
fun VitaCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
