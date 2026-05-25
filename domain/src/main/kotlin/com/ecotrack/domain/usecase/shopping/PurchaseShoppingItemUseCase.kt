package com.ecotrack.domain.usecase.shopping

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.repository.ProductRepository
import com.ecotrack.domain.repository.ShoppingRepository
import java.time.LocalDate
import javax.inject.Inject

class PurchaseShoppingItemUseCase @Inject constructor(
    private val shoppingRepository: ShoppingRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(itemId: Long): Boolean {
        val item = shoppingRepository.getItem(itemId) ?: return false
        val cleanName = item.name
            .replace(Regex("""\s*\(AI\)\s*$"""), "")
            .trim()
        if (cleanName.isBlank()) return false

        productRepository.addProduct(
            Product(
                id = 0,
                name = cleanName.replaceFirstChar { it.uppercase() },
                category = ProductCategory.OTHER,
                expiryDate = LocalDate.now().plusDays(7),
                quantity = 1.0,
                unit = "шт",
            ),
        )
        shoppingRepository.deleteItem(itemId)
        return true
    }
}
