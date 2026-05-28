package com.ecotrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ecotrack.core.database.entity.ConsumptionEventEntity
import com.ecotrack.core.database.model.ConsumptionEventTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionEventDao {
    @Insert
    suspend fun insert(event: ConsumptionEventEntity): Long

    @Query(
        """
        SELECT COUNT(*) FROM consumption_events
        WHERE eventType = :type
        """,
    )
    fun observeEventCount(type: ConsumptionEventTypeEntity): Flow<Int>
}
