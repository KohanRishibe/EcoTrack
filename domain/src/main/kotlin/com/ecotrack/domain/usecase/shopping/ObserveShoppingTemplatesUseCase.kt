package com.ecotrack.domain.usecase.shopping

import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShoppingTemplatesUseCase @Inject constructor(
    private val repository: ShoppingRepository,
) {
    operator fun invoke(): Flow<List<ShoppingItem>> = repository.observeTemplates()
}
