package com.ecotrack.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ecotrack.core.database.model.ProductCategoryEntity
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ProductCategoryEntity,
    val expiryDate: LocalDate,
    val quantity: Double,
    val unit: String,
    val barcode: String? = null,
    val imageUrl: String? = null,
    val usedCount: Int = 0,
    val wastedCount: Int = 0,
    val createdAt: Instant = Instant.now(),
)
