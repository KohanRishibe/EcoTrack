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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EcoPrimary,
    onPrimary = EcoOnPrimary,
    primaryContainer = EcoPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = EcoOnPrimary,
    secondary = EcoAccentTeal,
    onSecondary = Color.White,
    secondaryContainer = EcoSurfaceVariantLight,
    onSecondaryContainer = EcoOnSurfaceLight,
    tertiary = EcoAccentAmber,
    onTertiary = EcoOnSurfaceLight,
    background = EcoBackgroundLight,
    onBackground = EcoOnSurfaceLight,
    surface = EcoSurfaceLight,
    onSurface = EcoOnSurfaceLight,
    surfaceVariant = EcoSurfaceVariantLight,
    onSurfaceVariant = EcoOnSurfaceVariantLight,
    outline = EcoOutlineLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoPrimary,
    onPrimary = EcoOnPrimary,
    primaryContainer = EcoPrimary.copy(alpha = 0.22f),
    onPrimaryContainer = EcoPrimary,
    secondary = EcoAccentTeal,
    onSecondary = EcoOnPrimary,
    secondaryContainer = EcoSurfaceVariantDark,
    onSecondaryContainer = EcoOnSurfaceDark,
    tertiary = EcoAccentAmber,
    onTertiary = EcoOnPrimary,
    background = EcoBackgroundDark,
    onBackground = EcoOnSurfaceDark,
    surface = EcoSurfaceDark,
    onSurface = EcoOnSurfaceDark,
    surfaceVariant = EcoSurfaceVariantDark,
    onSurfaceVariant = EcoOnSurfaceVariantDark,
    outline = EcoOutlineDark,
)

@Immutable
data class EcoThemeConfig(
    val useDynamicColor: Boolean,
    val darkTheme: Boolean,
)

val LocalEcoThemeConfig = staticCompositionLocalOf {
    EcoThemeConfig(useDynamicColor = false, darkTheme = false)
}

val EcoShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun EcoTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        typography = ecoTypography(EcoFontFamily),
        shapes = EcoShapes,
        content = content,
    )
}
