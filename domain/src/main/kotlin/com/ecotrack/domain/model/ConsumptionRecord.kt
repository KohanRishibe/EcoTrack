package com.ecotrack.domain.model

data class ConsumptionRecord(
    val productName: String,
    val category: ProductCategory,
    val amount: Double,
    val unit: String,
    val eventType: ConsumptionEventType,
)
