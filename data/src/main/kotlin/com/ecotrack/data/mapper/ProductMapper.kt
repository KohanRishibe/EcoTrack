package com.ecotrack.data.mapper

import com.ecotrack.core.database.entity.ProductEntity
import com.ecotrack.core.database.model.ProductCategoryEntity
import com.ecotrack.core.network.dto.ProductDto
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import java.time.LocalDate

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    category = category.toDomain(),
    expiryDate = expiryDate,
    quantity = quantity,
    unit = unit,
    barcode = barcode,
    imageUrl = imageUrl,
    usedCount = usedCount,
    wastedCount = wastedCount,
    createdAt = createdAt,
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    category = category.toEntity(),
    expiryDate = expiryDate,
    quantity = quantity,
    unit = unit,
    barcode = barcode,
    imageUrl = imageUrl,
    usedCount = usedCount,
    wastedCount = wastedCount,
)

fun ProductCategoryEntity.toDomain(): ProductCategory = when (this) {
    ProductCategoryEntity.DAIRY -> ProductCategory.DAIRY
    ProductCategoryEntity.VEGETABLES -> ProductCategory.VEGETABLES
    ProductCategoryEntity.MEAT -> ProductCategory.MEAT
    ProductCategoryEntity.FRUITS -> ProductCategory.FRUITS
    ProductCategoryEntity.BAKERY -> ProductCategory.BAKERY
    ProductCategoryEntity.BEVERAGES -> ProductCategory.BEVERAGES
    ProductCategoryEntity.FROZEN -> ProductCategory.FROZEN
    ProductCategoryEntity.OTHER -> ProductCategory.OTHER
}

fun ProductCategory.toEntity(): ProductCategoryEntity = when (this) {
    ProductCategory.DAIRY -> ProductCategoryEntity.DAIRY
    ProductCategory.VEGETABLES -> ProductCategoryEntity.VEGETABLES
    ProductCategory.MEAT -> ProductCategoryEntity.MEAT
    ProductCategory.FRUITS -> ProductCategoryEntity.FRUITS
    ProductCategory.BAKERY -> ProductCategoryEntity.BAKERY
    ProductCategory.BEVERAGES -> ProductCategoryEntity.BEVERAGES
    ProductCategory.FROZEN -> ProductCategoryEntity.FROZEN
    ProductCategory.OTHER -> ProductCategoryEntity.OTHER
}

fun ProductDto.toDomain(barcode: String, defaultExpiry: LocalDate = LocalDate.now().plusDays(14)): Product =
    Product(
        id = 0,
        name = name,
        category = category?.let { ProductCategory.fromRaw(it) } ?: ProductCategory.OTHER,
        expiryDate = defaultExpiry,
        quantity = 1.0,
        unit = "шт",
        barcode = barcode,
        imageUrl = imageUrl,
    )
