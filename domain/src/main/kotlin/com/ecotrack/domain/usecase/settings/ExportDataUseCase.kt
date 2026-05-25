package com.ecotrack.domain.usecase.settings

import com.ecotrack.domain.repository.SettingsRepository
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(): String = repository.exportData()
}
