package com.ecotrack.domain.usecase.product

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.repository.ProductRepository
import javax.inject.Inject

class LookupBarcodeUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(barcode: String): Product? =
        repository.lookupBarcode(barcode)
}
