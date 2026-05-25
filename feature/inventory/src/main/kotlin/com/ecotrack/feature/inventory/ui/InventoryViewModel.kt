package com.ecotrack.feature.inventory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.usecase.product.AddProductUseCase
import com.ecotrack.domain.usecase.product.DeleteProductUseCase
import com.ecotrack.domain.usecase.product.ObserveProductsUseCase
import com.ecotrack.feature.inventory.domain.InventoryGrouped
import com.ecotrack.feature.inventory.domain.toGroupedUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryItemUi(
    val id: Long,
    val name: String,
    val category: ProductCategory,
    val categoryLabel: String,
    val expiryDateLabel: String,
    val expiryDate: java.time.LocalDate,
    val quantityLabel: String,
    val imageUrl: String?,
)

data class InventoryScreenUiState(
    val content: Resource<InventoryGrouped> = Resource.Loading,
    val isRefreshing: Boolean = false,
    val snackbar: SnackbarMessage? = null,
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val observeProducts: ObserveProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val addProductUseCase: AddProductUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryScreenUiState())
    val uiState: StateFlow<InventoryScreenUiState> = _uiState.asStateFlow()

    private var lastDeletedProduct: Product? = null
    private var observeJob: Job? = null

    init {
        observeProductsFlow()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        observeProductsFlow()
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }

    fun onDeleteProduct(item: InventoryItemUi, product: Product) {
        viewModelScope.launch {
            lastDeletedProduct = product
            runCatching { deleteProductUseCase(item.id) }
                .onSuccess {
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage("«${item.name}» удалён", "Отменить"))
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Не удалось удалить"))
                    }
                }
        }
    }

    fun undoDelete() {
        val product = lastDeletedProduct ?: return
        viewModelScope.launch {
            runCatching { addProductUseCase(product.copy(id = 0)) }
                .onSuccess {
                    lastDeletedProduct = null
                    dismissSnackbar()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Не удалось восстановить"))
                    }
                }
        }
    }

    private fun observeProductsFlow() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeProducts()
                .onStart {
                    if (!_uiState.value.isRefreshing) {
                        _uiState.update { it.copy(content = Resource.Loading) }
                    }
                }
                .map { products ->
                    if (products.isEmpty()) {
                        Resource.Success(InventoryGrouped(emptyMap(), emptyMap()))
                    } else {
                        Resource.Success(products.toGroupedUi())
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            content = Resource.Error(
                                message = e.message ?: "Ошибка загрузки запасов",
                                throwable = e,
                                isCritical = true,
                            ),
                            isRefreshing = false,
                        )
                    }
                }
                .collect { resource ->
                    _uiState.update {
                        it.copy(content = resource, isRefreshing = false)
                    }
                }
        }
    }
}
