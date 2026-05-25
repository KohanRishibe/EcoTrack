package com.ecotrack.feature.ai.ui.receipt

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.ai.ReceiptLineItem
import com.ecotrack.domain.model.ai.ReceiptScanResult
import com.ecotrack.domain.usecase.ai.ImportReceiptItemsUseCase
import com.ecotrack.domain.usecase.ai.ParseReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReceiptScanScreenUiState(
    val scanResult: Resource<ReceiptScanResult>? = null,
    val selectedIndices: Set<Int> = emptySet(),
    val isProcessing: Boolean = false,
    val isImporting: Boolean = false,
    val importDone: Boolean = false,
    val snackbar: SnackbarMessage? = null,
)

@HiltViewModel
class ReceiptScanViewModel @Inject constructor(
    private val parseReceipt: ParseReceiptUseCase,
    private val importReceiptItems: ImportReceiptItemsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptScanScreenUiState())
    val uiState: StateFlow<ReceiptScanScreenUiState> = _uiState.asStateFlow()

    fun dismissSnackbar() = _uiState.update { it.copy(snackbar = null) }

    fun onReceiptPhotoCaptured(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, scanResult = Resource.Loading) }
            runCatching { parseReceipt(bitmap) }
                .onSuccess { result ->
                    val indices = result.items.indices.toSet()
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            scanResult = Resource.Success(result),
                            selectedIndices = if (result.items.isEmpty()) emptySet() else indices,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            scanResult = Resource.Error(e.message ?: "Ошибка OCR", isCritical = false),
                            snackbar = SnackbarMessage(e.message ?: "Не удалось прочитать чек"),
                        )
                    }
                }
        }
    }

    fun toggleItem(index: Int) {
        _uiState.update { state ->
            val next = state.selectedIndices.toMutableSet()
            if (index in next) next.remove(index) else next.add(index)
            state.copy(selectedIndices = next)
        }
    }

    fun importSelected() {
        val result = (_uiState.value.scanResult as? Resource.Success)?.data ?: return
        val selected = _uiState.value.selectedIndices.mapNotNull { result.items.getOrNull(it) }
        if (selected.isEmpty()) {
            _uiState.update { it.copy(snackbar = SnackbarMessage("Выберите позиции")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }
            runCatching { importReceiptItems(selected) }
                .onSuccess { count ->
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            importDone = true,
                            snackbar = SnackbarMessage("Добавлено продуктов: $count"),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            snackbar = SnackbarMessage(e.message ?: "Ошибка импорта"),
                        )
                    }
                }
        }
    }

    fun reset() {
        _uiState.value = ReceiptScanScreenUiState()
    }
}
