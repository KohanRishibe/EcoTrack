package com.ecotrack.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ecotrack.core.database.model.ProductCategoryEntity

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ProductCategoryEntity = ProductCategoryEntity.OTHER,
    val quantity: Double = 1.0,
    val unit: String = "шт",
    val isChecked: Boolean = false,
    val isTemplate: Boolean = false,
    val sortOrder: Int = 0,
)
