package com.ecotrack.domain.usecase.product

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    operator fun invoke(id: Long): Flow<Product?> = repository.observeProduct(id)
}
