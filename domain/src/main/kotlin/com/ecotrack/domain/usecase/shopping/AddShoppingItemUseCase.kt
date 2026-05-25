package com.ecotrack.domain.usecase.shopping

import com.ecotrack.domain.repository.ShoppingRepository
import javax.inject.Inject

class AddShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingRepository,
) {
    suspend operator fun invoke(name: String) = repository.addItem(name)
}
