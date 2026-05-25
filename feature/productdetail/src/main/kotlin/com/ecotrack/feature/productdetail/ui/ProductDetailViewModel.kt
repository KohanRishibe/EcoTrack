package com.ecotrack.feature.productdetail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.ProductConsumeResult
import com.ecotrack.domain.usecase.product.MarkProductUsedUseCase
import com.ecotrack.domain.usecase.product.MarkProductWastedUseCase
import com.ecotrack.domain.usecase.product.ObserveProductUseCase
import com.ecotrack.feature.productdetail.domain.toDetailUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUi(
    val id: Long,
    val name: String,
    val categoryLabel: String,
    val expiryLabel: String,
    val quantityLabel: String,
    val usedCount: Int,
    val wastedCount: Int,
    val canConsume: Boolean,
    val imageUrl: String?,
)

data class ProductDetailScreenUiState(
    val content: Resource<ProductDetailUi> = Resource.Loading,
    val snackbar: SnackbarMessage? = null,
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeProduct: ObserveProductUseCase,
    private val markUsed: MarkProductUsedUseCase,
    private val markWasted: MarkProductWastedUseCase,
) : ViewModel() {

    /** ID продукта из type-safe Navigation (только примитив в аргументах). */
    private val productId: Long = savedStateHandle.get<Long>("productId") ?: 0L

    private val _uiState = MutableStateFlow(ProductDetailScreenUiState())
    val uiState: StateFlow<ProductDetailScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeProduct(productId)
                .onStart { _uiState.update { it.copy(content = Resource.Loading) } }
                .map { product ->
                    when (product) {
                        null -> Resource.Error(
                            message = "Продукт не найден",
                            isCritical = true,
                        )
                        else -> Resource.Success(product.toDetailUi())
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            content = Resource.Error(
                                message = e.message ?: "Ошибка загрузки",
                                throwable = e,
                                isCritical = true,
                            ),
                        )
                    }
                }
                .collect { resource ->
                    _uiState.update { it.copy(content = resource) }
                }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }

    fun onUsed() {
        consumeProduct { markUsed(productId) }
    }

    fun onWasted() {
        consumeProduct { markWasted(productId) }
    }

    private fun consumeProduct(action: suspend () -> ProductConsumeResult) {
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess { result ->
                    val message = when (result) {
                        ProductConsumeResult.CONSUMED -> "Списано 1 ед. с запасов"
                        ProductConsumeResult.DEPLETED_AND_REMOVED -> "Запас закончился — продукт удалён из списка"
                        ProductConsumeResult.ALREADY_EMPTY -> "Нечего списывать"
                    }
                    _uiState.update { it.copy(snackbar = SnackbarMessage(message)) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Ошибка"))
                    }
                }
        }
    }
}
