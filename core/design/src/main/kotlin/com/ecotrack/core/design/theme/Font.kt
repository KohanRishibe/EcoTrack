package com.ecotrack.core.design.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.ecotrack.core.design.R

/** Встроенный шрифт Comfortaa (variable) — всегда в APK, без Google Play Services. */
val EcoFontFamily: FontFamily = FontFamily(
    Font(R.font.eco_sans, FontWeight.Normal),
    Font(R.font.eco_sans, FontWeight.Medium),
    Font(R.font.eco_sans, FontWeight.SemiBold),
    Font(R.font.eco_sans, FontWeight.Bold),
)

val EcoReceiptFontFamily: FontFamily = FontFamily(
    Font(R.font.space_mono_regular, FontWeight.Normal),
)
