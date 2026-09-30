package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInliteTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.liteColorScheme
import androidx.compose.material3.dynamicliteColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = DarkBackground,
    primaryContainer = DarkEmeraldPrimaryContainer,
    onPrimaryContainer = DarkEmeraldPrimary,
    secondary = DarkSaffronSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = SaffronSecondary,
    onSecondary = SaffronOnSecondary,
    secondaryContainer = SaffronSecondaryContainer,
    onSecondaryContainer = SaffronOnSecondaryContainer,
    tertiary = SkyTertiary,
    onTertiary = SkyOnTertiary,
    tertiaryContainer = SkyTertiaryContainer,
    onTertiaryContainer = SkyOnTertiaryContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted rich palette for consistent teacher branding
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
