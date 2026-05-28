package com.ecotrack.feature.dashboard.domain

import com.ecotrack.domain.model.DashboardSummary
import com.ecotrack.feature.dashboard.ui.CategoryStatUi
import com.ecotrack.feature.dashboard.ui.DashboardContent

fun DashboardSummary.toUi(): DashboardContent {
    val consumptionTotal = usedCount + wastedCount
    val utilizationPercent = if (consumptionTotal > 0) {
        (usedCount * 100) / consumptionTotal
    } else {
        0
    }
    val maxCategory = categoryStats.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    return DashboardContent(
        totalProducts = totalProducts,
        totalUnits = totalUnits,
        expiringCount = expiringSoon.size,
        expiredCount = expiredCount,
        expiringItems = expiringSoon.map { it.name },
        usedCount = usedCount,
        wastedCount = wastedCount,
        utilizationPercent = utilizationPercent,
        categoryStats = categoryStats.map { stat ->
            CategoryStatUi(
                name = stat.name,
                count = stat.count,
                fraction = stat.count.toFloat() / maxCategory,
            )
        },
    )
}
