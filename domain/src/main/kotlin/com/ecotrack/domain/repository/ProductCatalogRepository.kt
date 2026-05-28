package com.ecotrack.domain.repository

import com.ecotrack.domain.model.ProductDefaultsSuggestion

interface ProductCatalogRepository {
    fun suggestFromName(productName: String): ProductDefaultsSuggestion
}
