package com.ecotrack.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.UserSettings
import com.ecotrack.domain.usecase.settings.ExportDataUseCase
import com.ecotrack.domain.usecase.settings.ObserveSettingsUseCase
import com.ecotrack.domain.usecase.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsScreenUiState(
    val settings: Resource<UserSettings> = Resource.Loading,
    val snackbar: SnackbarMessage? = null,
    val exportPreview: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeSettings: ObserveSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val exportData: ExportDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenUiState())
    val uiState: StateFlow<SettingsScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSettings()
                .onStart { _uiState.update { it.copy(settings = Resource.Loading) } }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            settings = Resource.Error(
                                message = e.message ?: "Ошибка загрузки настроек",
                                throwable = e,
                                isCritical = true,
                            ),
                        )
                    }
                }
                .collect { settings ->
                    _uiState.update { it.copy(settings = Resource.Success(settings)) }
                }
        }
    }

    fun update(settings: UserSettings) {
        viewModelScope.launch {
            runCatching { updateSettings(settings) }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Не удалось сохранить"))
                    }
                }
        }
    }

    fun export() {
        viewModelScope.launch {
            runCatching { exportData() }
                .onSuccess { result ->
                    _uiState.update { it.copy(exportPreview = result) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Ошибка экспорта"))
                    }
                }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }
}
