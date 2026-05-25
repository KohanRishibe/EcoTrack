package com.ecotrack.domain.model

import java.time.Instant
import java.time.LocalDate

data class Product(
    val id: Long,
    val name: String,
    val category: ProductCategory,
    val expiryDate: LocalDate,
    val quantity: Double,
    val unit: String,
    val barcode: String? = null,
    val imageUrl: String? = null,
    val usedCount: Int = 0,
    val wastedCount: Int = 0,
    val createdAt: Instant = Instant.now(),
)

enum class ProductCategory(val displayName: String) {
    DAIRY("Молочное"),
    VEGETABLES("Овощи"),
    MEAT("Мясо"),
    FRUITS("Фрукты"),
    BAKERY("Выпечка"),
    BEVERAGES("Напитки"),
    FROZEN("Заморозка"),
    OTHER("Прочее"),
    ;

    companion object {
        fun fromRaw(value: String): ProductCategory =
            entries.find { it.name == value || it.displayName == value } ?: OTHER
    }
}
