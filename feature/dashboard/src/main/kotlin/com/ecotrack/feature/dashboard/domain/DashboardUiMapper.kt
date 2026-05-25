package com.ecotrack.feature.dashboard.domain

import com.ecotrack.domain.model.DashboardSummary
import com.ecotrack.feature.dashboard.ui.DashboardContent

fun DashboardSummary.toUi(): DashboardContent = DashboardContent(
    greeting = greetingFor(userName),
    userName = userName,
    totalProducts = totalProducts,
    expiringCount = expiringSoon.size,
    expiringItems = expiringSoon.map { it.name },
    usedCount = usedCount,
    wastedCount = wastedCount,
)

private fun greetingFor(name: String): String {
    val hour = java.time.LocalTime.now().hour
    val timeGreeting = when (hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..22 -> "Добрый вечер"
        else -> "Доброй ночи"
    }
    return "$timeGreeting, $name"
}
