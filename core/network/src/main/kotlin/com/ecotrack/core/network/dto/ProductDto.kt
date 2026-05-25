package com.ecotrack.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val barcode: String,
    val name: String,
    val category: String? = null,
    val imageUrl: String? = null,
)
