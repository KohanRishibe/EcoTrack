package com.ecotrack.domain.model

data class CategoryStat(
    val name: String,
    val count: Int,
)

data class DashboardSummary(
    val totalProducts: Int,
    val totalUnits: Int,
    val expiringSoon: List<Product>,
    val expiredCount: Int,
    val usedCount: Int,
    val wastedCount: Int,
    val categoryStats: List<CategoryStat>,
)
