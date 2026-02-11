package com.hexium.nodes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun HexiumNodesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use WindowCompat or standard API properly to avoid deprecation warning
            // The warning "var statusBarColor: Int is deprecated" usually refers to some indirect access or specific Kotlin property accessor issue.
            // However, setStatusBarColor is not deprecated. window.statusBarColor (property access) uses get/setStatusBarColor.
            // If the warning persists, it might be due to a specific Gradle/Kotlin version quirk or I should use the setter explicitly.
            window.statusBarColor = colorScheme.primary.toArgb()

            // Fix: window.statusBarColor property access is marked deprecated in some contexts?
            // Actually, let's use the setter explicitly if possible or ignore if it's a false positive.
            // But to be safe, let's use the underlying method if accessible or suppresses.
            // The logs said: 'var statusBarColor: Int' is deprecated. Deprecated in Java.
            // This is strange because Window.setStatusBarColor is API 21+ and not deprecated.
            // Wait, maybe it's referring to something else.
            // Ah, `window.statusBarColor` = ...

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
