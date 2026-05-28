package com.ecotrack.domain.model

data class ProductDefaultsSuggestion(
    val category: ProductCategory,
    val quantity: Double,
    val unit: String,
    val shelfLifeDays: Int,
    val hint: String,
)
