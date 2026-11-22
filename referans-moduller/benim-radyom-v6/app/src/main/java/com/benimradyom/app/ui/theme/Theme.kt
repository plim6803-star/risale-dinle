package com.benimradyom.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Sakin, okunakli bir palet: koyu yesil-camii tonu + sicak toprak vurgusu.
val Teal900 = Color(0xFF12433C)
val Teal700 = Color(0xFF1E6B5E)
val Teal500 = Color(0xFF2E9584)
val Teal100 = Color(0xFFCDE9E2)

val Sand600 = Color(0xFFB9761F)
val Sand400 = Color(0xFFD98324)
val Sand100 = Color(0xFFF7E3C6)

val Ink900 = Color(0xFF10201D)
val Ink700 = Color(0xFF1B2F2B)
val Ink100 = Color(0xFFF3F7F5)

val SpeechBlue = Color(0xFF3D6FB4)
val SpotAmber = Color(0xFFD98324)
val BedGrey = Color(0xFF5B7A8C)
val LiveRed = Color(0xFFD64545)

private val LightScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,
    secondary = Sand600,
    onSecondary = Color.White,
    secondaryContainer = Sand100,
    onSecondaryContainer = Color(0xFF4A2D05),
    background = Ink100,
    onBackground = Ink900,
    surface = Color.White,
    onSurface = Ink900,
    surfaceVariant = Color(0xFFE3ECE8),
    onSurfaceVariant = Color(0xFF41544F),
    outline = Color(0xFF9FB3AD),
    error = LiveRed
)

private val DarkScheme = darkColorScheme(
    primary = Teal500,
    onPrimary = Color(0xFF00201B),
    primaryContainer = Teal900,
    onPrimaryContainer = Teal100,
    secondary = Sand400,
    onSecondary = Color(0xFF3A2200),
    secondaryContainer = Color(0xFF5C3A08),
    onSecondaryContainer = Sand100,
    background = Color(0xFF0C1615),
    onBackground = Color(0xFFDCE5E2),
    surface = Color(0xFF13201E),
    onSurface = Color(0xFFDCE5E2),
    surfaceVariant = Color(0xFF223330),
    onSurfaceVariant = Color(0xFFB4C4C0),
    outline = Color(0xFF6B807B),
    error = Color(0xFFFF8A80)
)

/** Liste renk paleti - yeni liste olustururken secilir. */
val playlistPalette = listOf(
    0xFF2E7D6F.toInt(), 0xFF3D6FB4.toInt(), 0xFF7B5EA7.toInt(), 0xFFD98324.toInt(),
    0xFF5B7A8C.toInt(), 0xFFB5495B.toInt(), 0xFF4C956C.toInt(), 0xFF8C6A3F.toInt()
)

@Composable
fun BenimRadyomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkScheme else LightScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = scheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
