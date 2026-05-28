package com.ecotrack.domain.model.ai

import com.ecotrack.domain.model.ProductCategory
import java.time.LocalDate

data class ProductPhotoInsight(
    val suggestedName: String,
    val category: ProductCategory,
    val suggestedQuantity: Double,
    val suggestedUnit: String,
    val suggestedShelfLifeDays: Int,
    val suggestedExpiryDate: LocalDate,
    val confidence: Float,
    val detectedLabels: List<String>,
    val source: AiSource,
)

enum class AiSource {
    ML_KIT,
    GEMINI_NANO,
}
