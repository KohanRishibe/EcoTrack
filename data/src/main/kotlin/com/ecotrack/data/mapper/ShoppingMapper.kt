package com.ecotrack.data.mapper

import com.ecotrack.core.database.entity.ShoppingItemEntity
import com.ecotrack.domain.model.ShoppingItem

fun ShoppingItemEntity.toDomain(): ShoppingItem = ShoppingItem(
    id = id,
    name = name,
    isChecked = isChecked,
    isTemplate = isTemplate,
)

fun ShoppingItem.toEntity(sortOrder: Int = 0): ShoppingItemEntity = ShoppingItemEntity(
    id = id,
    name = name,
    isChecked = isChecked,
    isTemplate = isTemplate,
    sortOrder = sortOrder,
)
