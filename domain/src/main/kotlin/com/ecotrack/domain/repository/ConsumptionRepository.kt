package com.ecotrack.domain.repository

import com.ecotrack.domain.model.ConsumptionRecord
import kotlinx.coroutines.flow.Flow

interface ConsumptionRepository {
    suspend fun recordConsumption(record: ConsumptionRecord)
    fun observeUsageTotals(): Flow<Pair<Int, Int>>
}
