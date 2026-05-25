package com.ecotrack.domain.usecase.product

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    operator fun invoke(): Flow<List<Product>> = repository.observeProducts()
}
