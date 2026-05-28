package com.ecotrack.domain.model

data class ShoppingItem(
    val id: Long,
    val name: String,
    val category: ProductCategory,
    val quantity: Double,
    val unit: String,
    val isChecked: Boolean,
    val isTemplate: Boolean,
)
