package com.ecotrack.domain.usecase.ai

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ai.ReceiptLineItem
import com.ecotrack.domain.repository.ProductRepository
import java.time.LocalDate
import javax.inject.Inject

class ImportReceiptItemsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(items: List<ReceiptLineItem>): Int {
        var imported = 0
        items.forEach { line ->
            val name = line.name.trim()
            if (name.length < 2) return@forEach
            productRepository.addProduct(
                Product(
                    id = 0,
                    name = name.replaceFirstChar { it.uppercase() },
                    category = com.ecotrack.domain.model.ProductCategory.OTHER,
                    expiryDate = LocalDate.now().plusDays(7),
                    quantity = line.quantity ?: 1.0,
                    unit = line.unit ?: "шт",
                ),
            )
            imported++
        }
        return imported
    }
}
