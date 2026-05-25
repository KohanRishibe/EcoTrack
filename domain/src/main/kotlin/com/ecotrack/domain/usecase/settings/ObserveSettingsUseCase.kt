package com.ecotrack.domain.usecase.settings

import com.ecotrack.domain.model.UserSettings
import com.ecotrack.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}
