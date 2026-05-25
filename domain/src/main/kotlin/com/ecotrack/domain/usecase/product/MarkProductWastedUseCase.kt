package com.ecotrack.domain.usecase.product

import com.ecotrack.domain.repository.ProductRepository
import javax.inject.Inject

class MarkProductWastedUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(id: Long) = repository.markWasted(id)
}
