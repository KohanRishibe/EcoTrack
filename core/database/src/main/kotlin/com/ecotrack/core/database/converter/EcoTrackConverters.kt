package com.ecotrack.core.database.converter

import androidx.room.TypeConverter
import com.ecotrack.core.database.model.ConsumptionEventTypeEntity
import com.ecotrack.core.database.model.ProductCategoryEntity
import java.time.Instant
import java.time.LocalDate

class EcoTrackConverters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? =
        value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? =
        date?.toEpochDay()

    @TypeConverter
    fun fromInstant(value: Long?): Instant? =
        value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toInstant(instant: Instant?): Long? =
        instant?.toEpochMilli()

    @TypeConverter
    fun fromCategory(value: String?): ProductCategoryEntity? =
        value?.let { runCatching { ProductCategoryEntity.valueOf(it) }.getOrDefault(ProductCategoryEntity.OTHER) }

    @TypeConverter
    fun toCategory(category: ProductCategoryEntity?): String? =
        category?.name

    @TypeConverter
    fun fromEventType(value: String?): ConsumptionEventTypeEntity? =
        value?.let { runCatching { ConsumptionEventTypeEntity.valueOf(it) }.getOrDefault(ConsumptionEventTypeEntity.USED) }

    @TypeConverter
    fun toEventType(type: ConsumptionEventTypeEntity?): String? =
        type?.name
}
