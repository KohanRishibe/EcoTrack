package com.ecotrack.feature.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.model.ai.SmartShoppingSuggestion
import com.ecotrack.domain.usecase.ai.ApplySmartSuggestionsUseCase
import com.ecotrack.domain.usecase.ai.GetSmartShoppingSuggestionsUseCase
import com.ecotrack.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.ecotrack.domain.usecase.shopping.ObserveShoppingItemsUseCase
import com.ecotrack.domain.usecase.shopping.PurchaseShoppingItemUseCase
import com.ecotrack.feature.dashboard.domain.toUi
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

data class DashboardContent(
    val greeting: String,
    val userName: String,
    val totalProducts: Int,
    val expiringCount: Int,
    val expiringItems: List<String>,
    val usedCount: Int,
    val wastedCount: Int,
)

data class DashboardScreenUiState(
    val content: Resource<DashboardContent> = Resource.Loading,
    val isRefreshing: Boolean = false,
    val cartItems: List<ShoppingItem> = emptyList(),
    val purchasingItemId: Long? = null,
    val smartSuggestions: List<SmartShoppingSuggestion> = emptyList(),
    val smartSuggestionsLoading: Boolean = false,
    val isApplyingSuggestions: Boolean = false,
    val snackbar: SnackbarMessage? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummary: GetDashboardSummaryUseCase,
    private val observeCartItems: ObserveShoppingItemsUseCase,
    private val purchaseShoppingItem: PurchaseShoppingItemUseCase,
    private val getSmartSuggestions: GetSmartShoppingSuggestionsUseCase,
    private val applySmartSuggestions: ApplySmartSuggestionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardScreenUiState())
    val uiState: StateFlow<DashboardScreenUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var cartJob: Job? = null

    init {
        observeDashboard()
        observeCart()
        loadSmartSuggestions()
    }

    fun dismissSnackbar() = _uiState.update { it.copy(snackbar = null) }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        observeDashboard()
        observeCart()
        loadSmartSuggestions()
    }

    fun purchaseCartItem(itemId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(purchasingItemId = itemId) }
            runCatching { purchaseShoppingItem(itemId) }
                .onSuccess { purchased ->
                    _uiState.update {
                        it.copy(
                            purchasingItemId = null,
                            snackbar = if (purchased) {
                                SnackbarMessage("Куплено и добавлено в запасы")
                            } else {
                                SnackbarMessage("Не удалось оформить покупку")
                            },
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            purchasingItemId = null,
                            snackbar = SnackbarMessage(e.message ?: "Ошибка покупки"),
                        )
                    }
                }
        }
    }

    private fun observeCart() {
        cartJob?.cancel()
        cartJob = viewModelScope.launch {
            observeCartItems()
                .catch { /* корзина опциональна на главной */ }
                .collect { items ->
                    _uiState.update { it.copy(cartItems = items) }
                }
        }
    }

    fun applySmartSuggestionsToShoppingList() {
        val suggestions = _uiState.value.smartSuggestions
        if (suggestions.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isApplyingSuggestions = true) }
            runCatching { applySmartSuggestions(suggestions) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isApplyingSuggestions = false,
                            smartSuggestions = emptyList(),
                            snackbar = SnackbarMessage("В список покупок добавлено: ${suggestions.size}"),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isApplyingSuggestions = false,
                            snackbar = SnackbarMessage(e.message ?: "Не удалось добавить"),
                        )
                    }
                }
        }
    }

    private fun loadSmartSuggestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(smartSuggestionsLoading = true) }
            runCatching { getSmartSuggestions() }
                .onSuccess { list ->
                    _uiState.update { it.copy(smartSuggestions = list, smartSuggestionsLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(smartSuggestionsLoading = false) }
                }
        }
    }

    private fun observeDashboard() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            getDashboardSummary()
                .onStart {
                    if (!_uiState.value.isRefreshing) {
                        _uiState.update { it.copy(content = Resource.Loading) }
                    }
                }
                .map { summary ->
                    val content = summary.toUi()
                    Resource.Success(content)
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            content = Resource.Error(
                                message = e.message ?: "Ошибка загрузки",
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
