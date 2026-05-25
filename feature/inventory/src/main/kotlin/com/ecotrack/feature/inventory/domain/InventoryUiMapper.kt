package com.ecotrack.feature.inventory.domain

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.feature.inventory.ui.InventoryItemUi
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun Product.toUi(): InventoryItemUi = InventoryItemUi(
    id = id,
    name = name,
    category = category,
    categoryLabel = category.displayName,
    expiryDateLabel = expiryDate.format(dateFormatter),
    expiryDate = expiryDate,
    quantityLabel = "${quantity.toInt()} ${unit}",
    imageUrl = imageUrl,
)

data class InventoryGrouped(
    val groups: Map<ProductCategory, List<InventoryItemUi>>,
    val productsById: Map<Long, Product>,
)

fun List<Product>.toGroupedUi(): InventoryGrouped {
    val productsById = associateBy { it.id }
    val groups = map { it.toUi() }
        .groupBy { it.category }
        .toSortedMap(compareBy { it.ordinal })
    return InventoryGrouped(groups, productsById)
}
