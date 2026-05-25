package com.ecotrack.core.ui.util

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Минимальная зона нажатия по рекомендациям Material (48.dp). */
fun Modifier.ecoTouchTarget(): Modifier = defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
