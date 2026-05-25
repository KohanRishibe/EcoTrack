package com.ecotrack.domain.model.ai

import java.time.LocalDate

data class SmartShoppingSuggestion(
    val productId: Long,
    val productName: String,
    val reason: String,
    val predictedRunOutDate: LocalDate,
    val suggestedQuantity: String,
)
