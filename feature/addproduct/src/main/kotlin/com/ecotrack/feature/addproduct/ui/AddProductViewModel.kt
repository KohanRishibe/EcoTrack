package com.ecotrack.feature.addproduct.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.usecase.product.AddProductUseCase
import com.ecotrack.domain.usecase.product.LookupBarcodeUseCase
import com.ecotrack.domain.usecase.product.SuggestProductDefaultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val unitExpanded: Boolean = false,
    val aiHint: String? = null,
    val isAiSuggesting: Boolean = false,
    val isSaving: Boolean = false,
    val snackbar: SnackbarMessage? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class AddProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addProduct: AddProductUseCase,
    private val lookupBarcode: LookupBarcodeUseCase,
    private val suggestDefaults: SuggestProductDefaultsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductScreenUiState())
    val uiState: StateFlow<AddProductScreenUiState> = _uiState.asStateFlow()

    private var suggestJob: Job? = null
    private var userLockedCategory = false
    private var userLockedQuantity = false
    private var userLockedUnit = false

    init {
        savedStateHandle.get<String>("barcode")?.let { barcode ->
            applyBarcode(barcode, lookupRemote = true)
        }
        applyNavigationSuggestions(savedStateHandle)
    }

    private fun applyNavigationSuggestions(savedStateHandle: SavedStateHandle) {
        val suggestedName = savedStateHandle.get<String>("suggestedName")
        val suggestedCategory = savedStateHandle.get<String>("suggestedCategory")
        val suggestedExpiry = savedStateHandle.get<String>("suggestedExpiryDate")
        val suggestedQuantity = savedStateHandle.get<String>("suggestedQuantity")
        val suggestedUnit = savedStateHandle.get<String>("suggestedUnit")

        if (suggestedName != null || suggestedCategory != null || suggestedExpiry != null ||
            suggestedQuantity != null || suggestedUnit != null
        ) {
            userLockedCategory = suggestedCategory != null
            userLockedQuantity = suggestedQuantity != null
            userLockedUnit = suggestedUnit != null
            _uiState.update {
                it.copy(
                    name = suggestedName ?: it.name,
                    category = suggestedCategory?.let(ProductCategory::fromRaw) ?: it.category,
                    expiryDate = suggestedExpiry?.let(LocalDate::parse) ?: it.expiryDate,
                    quantity = suggestedQuantity ?: it.quantity,
                    unit = suggestedUnit ?: it.unit,
                    aiHint = "Заполнено по фото (AI)",
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
        requestAiSuggestion(value)
    }

    fun onCategoryChange(value: ProductCategory) {
        userLockedCategory = true
        _uiState.update { it.copy(category = value) }
        if (!userLockedQuantity && !userLockedUnit) {
            applySuggestionForName(_uiState.value.name, respectLocks = true)
        }
    }

    fun onExpiryDateChange(value: LocalDate) =
        _uiState.update { it.copy(expiryDate = value, showDatePicker = false) }

    fun onQuantityChange(value: String) {
        userLockedQuantity = true
        _uiState.update { it.copy(quantity = value) }
    }

    fun onCategoryExpandedChange(expanded: Boolean) =
        _uiState.update { it.copy(categoryExpanded = expanded) }

    fun onUnitChange(value: String) {
        userLockedUnit = true
        _uiState.update { it.copy(unit = value) }
    }

    fun onUnitExpandedChange(expanded: Boolean) =
        _uiState.update { it.copy(unitExpanded = expanded) }

    fun onShowDatePickerChange(show: Boolean) =
        _uiState.update { it.copy(showDatePicker = show) }

    fun dismissSnackbar() = _uiState.update { it.copy(snackbar = null) }

    fun applyAiSuggestionNow() {
        applySuggestionForName(_uiState.value.name, respectLocks = false, force = true)
    }

    private fun requestAiSuggestion(name: String) {
        suggestJob?.cancel()
        if (name.trim().length < 2) {
            _uiState.update { it.copy(aiHint = null, isAiSuggesting = false) }
            return
        }
        suggestJob = viewModelScope.launch {
            _uiState.update { it.copy(isAiSuggesting = true) }
            delay(350)
            if (_uiState.value.name != name) return@launch
            applySuggestionForName(name, respectLocks = true)
            _uiState.update { it.copy(isAiSuggesting = false) }
        }
    }

    private fun applySuggestionForName(
        name: String,
        respectLocks: Boolean,
        force: Boolean = false,
    ) {
        val suggestion = suggestDefaults(name) ?: return
        _uiState.update { state ->
            state.copy(
                category = if (!respectLocks || force || !userLockedCategory) suggestion.category else state.category,
                quantity = if (!respectLocks || force || !userLockedQuantity) {
                    formatQuantityInput(suggestion.quantity)
                } else {
                    state.quantity
                },
                unit = if (!respectLocks || force || !userLockedUnit) suggestion.unit else state.unit,
                expiryDate = if (!respectLocks || force) {
                    LocalDate.now().plusDays(suggestion.shelfLifeDays.toLong())
                } else {
                    state.expiryDate
                },
                aiHint = suggestion.hint,
            )
        }
    }

    private fun applyBarcode(barcode: String, lookupRemote: Boolean) {
        _uiState.update { it.copy(barcode = barcode) }
        if (!lookupRemote) return
        viewModelScope.launch {
            lookupBarcode(barcode)?.let { product ->
                userLockedCategory = true
                userLockedQuantity = true
                userLockedUnit = true
                _uiState.update {
                    it.copy(
                        name = product.name,
                        category = product.category,
                        quantity = formatQuantityInput(product.quantity),
                        unit = product.unit,
                        barcode = barcode,
                        aiHint = "Данные со штрихкода",
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
        val quantity = state.quantity.replace(',', '.').toDoubleOrNull()
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

    private fun formatQuantityInput(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else ProductQuantity.formatDecimal(value)
}
