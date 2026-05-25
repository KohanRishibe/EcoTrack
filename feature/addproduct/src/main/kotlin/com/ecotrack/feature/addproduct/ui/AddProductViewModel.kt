package com.ecotrack.feature.addproduct.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.usecase.product.AddProductUseCase
import com.ecotrack.domain.usecase.product.LookupBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddProductScreenUiState(
    val name: String = "",
    val category: ProductCategory = ProductCategory.OTHER,
    val expiryDate: LocalDate = LocalDate.now().plusDays(7),
    val quantity: String = "1",
    val unit: String = "шт",
    val barcode: String? = null,
    val showDatePicker: Boolean = false,
    val categoryExpanded: Boolean = false,
    val isSaving: Boolean = false,
    val snackbar: SnackbarMessage? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class AddProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addProduct: AddProductUseCase,
    private val lookupBarcode: LookupBarcodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductScreenUiState())
    val uiState: StateFlow<AddProductScreenUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("barcode")?.let { barcode ->
            applyBarcode(barcode, lookupRemote = true)
        }
        val suggestedName = savedStateHandle.get<String>("suggestedName")
        val suggestedCategory = savedStateHandle.get<String>("suggestedCategory")
        val suggestedExpiry = savedStateHandle.get<String>("suggestedExpiryDate")
        if (suggestedName != null || suggestedCategory != null || suggestedExpiry != null) {
            _uiState.update {
                it.copy(
                    name = suggestedName ?: it.name,
                    category = suggestedCategory?.let(ProductCategory::fromRaw) ?: it.category,
                    expiryDate = suggestedExpiry?.let(LocalDate::parse) ?: it.expiryDate,
                )
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onCategoryChange(value: ProductCategory) = _uiState.update { it.copy(category = value) }
    fun onExpiryDateChange(value: LocalDate) = _uiState.update { it.copy(expiryDate = value, showDatePicker = false) }
    fun onQuantityChange(value: String) = _uiState.update { it.copy(quantity = value) }
    fun onCategoryExpandedChange(expanded: Boolean) = _uiState.update { it.copy(categoryExpanded = expanded) }
    fun onShowDatePickerChange(show: Boolean) = _uiState.update { it.copy(showDatePicker = show) }
    fun dismissSnackbar() = _uiState.update { it.copy(snackbar = null) }

    private fun applyBarcode(barcode: String, lookupRemote: Boolean) {
        _uiState.update { it.copy(barcode = barcode) }
        if (!lookupRemote) return
        viewModelScope.launch {
            lookupBarcode(barcode)?.let { product ->
                _uiState.update {
                    it.copy(
                        name = product.name,
                        category = product.category,
                        barcode = barcode,
                    )
                }
            }
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(snackbar = SnackbarMessage("Введите название продукта")) }
            return
        }
        val quantity = state.quantity.toDoubleOrNull()
        if (quantity == null || quantity <= 0) {
            _uiState.update { it.copy(snackbar = SnackbarMessage("Некорректное количество")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, snackbar = null) }
            runCatching {
                addProduct(
                    Product(
                        id = 0,
                        name = state.name.trim(),
                        category = state.category,
                        expiryDate = state.expiryDate,
                        quantity = quantity,
                        unit = state.unit,
                        barcode = state.barcode,
                    ),
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        snackbar = SnackbarMessage(e.message ?: "Не удалось сохранить"),
                    )
                }
            }
        }
    }
}
