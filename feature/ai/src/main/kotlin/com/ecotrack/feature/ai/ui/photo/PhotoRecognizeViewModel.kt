package com.ecotrack.feature.ai.ui.photo

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.ai.ProductPhotoInsight
import com.ecotrack.domain.usecase.ai.RecognizeProductFromPhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoRecognizeScreenUiState(
    val insight: Resource<ProductPhotoInsight>? = null,
    val isProcessing: Boolean = false,
    val snackbar: SnackbarMessage? = null,
)

@HiltViewModel
class PhotoRecognizeViewModel @Inject constructor(
    private val recognizeProduct: RecognizeProductFromPhotoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoRecognizeScreenUiState())
    val uiState: StateFlow<PhotoRecognizeScreenUiState> = _uiState.asStateFlow()

    fun dismissSnackbar() = _uiState.update { it.copy(snackbar = null) }

    fun onPhotoCaptured(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, insight = Resource.Loading) }
            runCatching { recognizeProduct(bitmap) }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            insight = if (result != null) {
                                Resource.Success(result)
                            } else {
                                Resource.Error("Не удалось распознать продукт", isCritical = false)
                            },
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            insight = Resource.Error(e.message ?: "Ошибка", isCritical = false),
                            snackbar = SnackbarMessage(e.message ?: "Ошибка распознавания"),
                        )
                    }
                }
        }
    }

    fun reset() {
        _uiState.value = PhotoRecognizeScreenUiState()
    }
}
