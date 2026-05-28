package com.ecotrack.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ecotrack.core.database.model.ConsumptionEventTypeEntity
import com.ecotrack.core.database.model.ProductCategoryEntity
import java.time.Instant

@Entity(tableName = "consumption_events")
data class ConsumptionEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val category: ProductCategoryEntity,
    val amount: Double,
    val unit: String,
    val eventType: ConsumptionEventTypeEntity,
    val recordedAt: Instant = Instant.now(),
)
