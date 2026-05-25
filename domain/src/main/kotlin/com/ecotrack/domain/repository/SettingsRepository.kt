package com.ecotrack.domain.repository

import com.ecotrack.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun exportData(): String
}
