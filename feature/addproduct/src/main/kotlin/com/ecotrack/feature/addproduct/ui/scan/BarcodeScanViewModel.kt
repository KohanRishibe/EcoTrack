package com.ecotrack.feature.addproduct.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.usecase.product.GetProductByBarcodeUseCase
import com.ecotrack.feature.addproduct.ui.scan.mapper.toScannedProductCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannedProductCardUi(
    val productId: Long,
    val barcode: String,
    val name: String,
    val categoryLabel: String,
    val expiryLabel: String,
    val quantityLabel: String,
)

data class BarcodeScanScreenUiState(
    val isScanningActive: Boolean = true,
    val scannedProduct: ScannedProductCardUi? = null,
    val lastBarcode: String? = null,
    val snackbar: SnackbarMessage? = null,
)

sealed interface BarcodeScanEvent {
    data class OpenAddProduct(val barcode: String) : BarcodeScanEvent
    data class OpenProductDetail(val productId: Long) : BarcodeScanEvent
}

@HiltViewModel
class BarcodeScanViewModel @Inject constructor(
    private val getProductByBarcode: GetProductByBarcodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarcodeScanScreenUiState())
    val uiState: StateFlow<BarcodeScanScreenUiState> = _uiState.asStateFlow()

    private val _events = Channel<BarcodeScanEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var lastScanTimestamp = 0L

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }

    fun onBarcodeScanned(barcode: String) {
        val now = System.currentTimeMillis()
        if (now - lastScanTimestamp < SCAN_COOLDOWN_MS) return
        if (!_uiState.value.isScanningActive) return

        lastScanTimestamp = now
        _uiState.update {
            it.copy(
                isScanningActive = false,
                lastBarcode = barcode,
            )
        }

        viewModelScope.launch {
            runCatching { getProductByBarcode(barcode) }
                .onSuccess { product ->
                    if (product != null) {
                        _uiState.update {
                            it.copy(scannedProduct = product.toScannedProductCardUi(barcode))
                        }
                    } else {
                        _events.send(BarcodeScanEvent.OpenAddProduct(barcode))
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            snackbar = SnackbarMessage(e.message ?: "Ошибка поиска"),
                            isScanningActive = true,
                        )
                    }
                }
        }
    }

    fun onDismissProductCard() {
        _uiState.update {
            it.copy(scannedProduct = null, isScanningActive = true, lastBarcode = null)
        }
        lastScanTimestamp = System.currentTimeMillis()
    }

    fun onOpenProductDetail() {
        val productId = _uiState.value.scannedProduct?.productId ?: return
        viewModelScope.launch {
            _events.send(BarcodeScanEvent.OpenProductDetail(productId))
        }
    }

    fun onScanAgain() {
        _uiState.update {
            it.copy(scannedProduct = null, isScanningActive = true, lastBarcode = null)
        }
        lastScanTimestamp = System.currentTimeMillis()
    }

    companion object {
        private const val SCAN_COOLDOWN_MS = 2_000L
    }
}
