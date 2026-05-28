package com.ecotrack.domain.usecase.product

import com.ecotrack.domain.model.ProductDefaultsSuggestion
import com.ecotrack.domain.repository.ProductCatalogRepository
import javax.inject.Inject

class SuggestProductDefaultsUseCase @Inject constructor(
    private val catalog: ProductCatalogRepository,
) {
    operator fun invoke(productName: String): ProductDefaultsSuggestion? {
        val trimmed = productName.trim()
        if (trimmed.length < 2) return null
        return catalog.suggestFromName(trimmed)
    }
}
