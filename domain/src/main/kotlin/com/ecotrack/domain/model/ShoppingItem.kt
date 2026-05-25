package com.ecotrack.domain.model

data class ShoppingItem(
    val id: Long,
    val name: String,
    val isChecked: Boolean,
    val isTemplate: Boolean,
)
