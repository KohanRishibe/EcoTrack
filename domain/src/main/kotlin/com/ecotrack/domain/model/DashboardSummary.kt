package com.ecotrack.domain.model

data class DashboardSummary(
    val userName: String,
    val totalProducts: Int,
    val expiringSoon: List<Product>,
    val usedCount: Int,
    val wastedCount: Int,
)
