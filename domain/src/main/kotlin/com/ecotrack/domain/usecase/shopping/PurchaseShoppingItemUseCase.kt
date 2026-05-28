package com.ecotrack.domain.usecase.shopping

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.repository.ProductRepository
import com.ecotrack.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class PurchaseShoppingItemUseCase @Inject constructor(
    private val shoppingRepository: ShoppingRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(itemId: Long): Boolean {
        val item = shoppingRepository.getItem(itemId) ?: return false
        if (item.name.isBlank()) return false

        val products = productRepository.observeProducts().first()
        val existing = products.find { it.name.equals(item.name, ignoreCase = true) }

        if (existing != null) {
            productRepository.updateProduct(
                existing.copy(
                    quantity = existing.quantity + item.quantity,
                    unit = item.unit,
                    category = item.category,
                    expiryDate = LocalDate.now().plusDays(shelfLifeDaysFor(item.category)),
                ),
            )
        } else {
            productRepository.addProduct(
                Product(
                    id = 0,
                    name = item.name,
                    category = item.category,
                    expiryDate = LocalDate.now().plusDays(shelfLifeDaysFor(item.category)),
                    quantity = item.quantity,
                    unit = item.unit,
                ),
            )
        }

        shoppingRepository.deleteItem(itemId)
        return true
    }

    private fun shelfLifeDaysFor(category: ProductCategory): Long = when (category) {
        ProductCategory.DAIRY -> 7
        ProductCategory.VEGETABLES -> 5
        ProductCategory.MEAT -> 3
        ProductCategory.FRUITS -> 7
        ProductCategory.BAKERY -> 4
        ProductCategory.BEVERAGES -> 30
        ProductCategory.FROZEN -> 90
        ProductCategory.OTHER -> 7
    }
}
