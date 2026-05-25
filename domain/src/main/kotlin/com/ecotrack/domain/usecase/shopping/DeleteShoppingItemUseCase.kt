package com.ecotrack.domain.usecase.shopping

import com.ecotrack.domain.repository.ShoppingRepository
import javax.inject.Inject

class DeleteShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteItem(id)
}
