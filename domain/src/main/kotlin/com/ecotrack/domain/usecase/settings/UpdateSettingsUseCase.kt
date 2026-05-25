package com.ecotrack.domain.usecase.settings

import com.ecotrack.domain.model.UserSettings
import com.ecotrack.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(settings: UserSettings) =
        repository.updateSettings(settings)
}
