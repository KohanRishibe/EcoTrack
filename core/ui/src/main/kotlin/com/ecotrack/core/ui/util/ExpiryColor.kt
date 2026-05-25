package com.ecotrack.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ecotrack.core.design.theme.ExpiryCritical
import com.ecotrack.core.design.theme.ExpiryFresh
import com.ecotrack.core.design.theme.ExpiryWarning
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class ExpiryStatus { FRESH, WARNING, CRITICAL }

fun expiryStatus(expiryDate: LocalDate, today: LocalDate = LocalDate.now()): ExpiryStatus {
    val days = ChronoUnit.DAYS.between(today, expiryDate)
    return when {
        days < 0 -> ExpiryStatus.CRITICAL
        days <= 2 -> ExpiryStatus.CRITICAL
        days <= 7 -> ExpiryStatus.WARNING
        else -> ExpiryStatus.FRESH
    }
}

@Composable
fun expiryColor(status: ExpiryStatus): Color = when (status) {
    ExpiryStatus.FRESH -> ExpiryFresh
    ExpiryStatus.WARNING -> ExpiryWarning
    ExpiryStatus.CRITICAL -> ExpiryCritical
}
