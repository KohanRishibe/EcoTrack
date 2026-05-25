package com.ecotrack.core.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EcoGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = EcoGreenLight,
    onPrimaryContainer = EcoGreenDark,
    secondary = EcoSand,
    onSecondary = EcoGreenDark,
    secondaryContainer = EcoCream,
    onSecondaryContainer = EcoGreenDark,
    background = EcoCream,
    onBackground = EcoGreenDark,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = EcoGreenDark,
    surfaceVariant = EcoSand.copy(alpha = 0.3f),
    onSurfaceVariant = EcoGreenDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoMutedGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = EcoGreenDark,
    onPrimaryContainer = EcoGreenLight,
    secondary = EcoSand.copy(alpha = 0.7f),
    onSecondary = EcoCream,
    secondaryContainer = EcoDarkSurface,
    onSecondaryContainer = EcoCream,
    background = EcoDarkBackground,
    onBackground = EcoCream,
    surface = EcoDarkSurface,
    onSurface = EcoCream,
    surfaceVariant = EcoDarkSurface,
    onSurfaceVariant = EcoSand,
)

@Immutable
data class EcoThemeConfig(
    val useDynamicColor: Boolean,
    val darkTheme: Boolean,
)

val LocalEcoThemeConfig = staticCompositionLocalOf {
    EcoThemeConfig(useDynamicColor = true, darkTheme = false)
}

val EcoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun EcoTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
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
        typography = EcoTypography,
        shapes = EcoShapes,
        content = content,
    )
}
