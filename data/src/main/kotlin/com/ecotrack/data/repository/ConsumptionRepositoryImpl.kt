package com.ecotrack.data.repository

import com.ecotrack.core.database.dao.ConsumptionEventDao
import com.ecotrack.core.database.entity.ConsumptionEventEntity
import com.ecotrack.core.database.model.ConsumptionEventTypeEntity
import com.ecotrack.data.mapper.toEntity
import com.ecotrack.domain.model.ConsumptionEventType
import com.ecotrack.domain.model.ConsumptionRecord
import com.ecotrack.domain.repository.ConsumptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsumptionRepositoryImpl @Inject constructor(
    private val consumptionEventDao: ConsumptionEventDao,
) : ConsumptionRepository {

    override suspend fun recordConsumption(record: ConsumptionRecord) {
        consumptionEventDao.insert(
            ConsumptionEventEntity(
                productName = record.productName.trim(),
                category = record.category.toEntity(),
                amount = record.amount,
                unit = record.unit,
                eventType = when (record.eventType) {
                    ConsumptionEventType.USED -> ConsumptionEventTypeEntity.USED
                    ConsumptionEventType.WASTED -> ConsumptionEventTypeEntity.WASTED
                },
            ),
        )
    }

    override fun observeUsageTotals(): Flow<Pair<Int, Int>> = combine(
        consumptionEventDao.observeEventCount(ConsumptionEventTypeEntity.USED),
        consumptionEventDao.observeEventCount(ConsumptionEventTypeEntity.WASTED),
    ) { used, wasted -> used to wasted }
}
