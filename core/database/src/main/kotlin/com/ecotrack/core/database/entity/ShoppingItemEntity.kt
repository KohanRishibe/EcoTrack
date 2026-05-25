package com.ecotrack.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isChecked: Boolean = false,
    val isTemplate: Boolean = false,
    val sortOrder: Int = 0,
)
