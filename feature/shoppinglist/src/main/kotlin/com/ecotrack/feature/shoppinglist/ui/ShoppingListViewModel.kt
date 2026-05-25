package com.ecotrack.feature.shoppinglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.SnackbarMessage
import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.usecase.shopping.AddShoppingItemUseCase
import com.ecotrack.domain.usecase.shopping.ObserveShoppingItemsUseCase
import com.ecotrack.domain.usecase.shopping.ObserveShoppingTemplatesUseCase
import com.ecotrack.domain.usecase.shopping.DeleteShoppingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingListContent(
    val items: List<ShoppingItem>,
    val templates: List<ShoppingItem>,
)

data class ShoppingListScreenUiState(
    val content: Resource<ShoppingListContent> = Resource.Loading,
    val isRefreshing: Boolean = false,
    val newItemText: String = "",
    val snackbar: SnackbarMessage? = null,
)

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val observeItems: ObserveShoppingItemsUseCase,
    private val observeTemplates: ObserveShoppingTemplatesUseCase,
    private val addItem: AddShoppingItemUseCase,
    private val deleteItem: DeleteShoppingItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListScreenUiState())
    val uiState: StateFlow<ShoppingListScreenUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observe()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        observe()
    }

    fun onNewItemTextChange(text: String) {
        _uiState.update { it.copy(newItemText = text) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }

    fun addQuickItem() {
        val name = _uiState.value.newItemText.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            runCatching { addItem(name) }
                .onSuccess {
                    _uiState.update { it.copy(newItemText = "") }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Не удалось добавить"))
                    }
                }
        }
    }

    fun addFromTemplate(template: ShoppingItem) {
        viewModelScope.launch {
            runCatching { addItem(template.name) }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Не удалось добавить"))
                    }
                }
        }
    }

    fun removeFromCart(item: ShoppingItem) {
        viewModelScope.launch {
            runCatching { deleteItem(item.id) }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(snackbar = SnackbarMessage(e.message ?: "Не удалось удалить"))
                    }
                }
        }
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                observeItems(),
                observeTemplates(),
            ) { items, templates ->
                ShoppingListContent(items, templates)
            }
                .onStart {
                    if (!_uiState.value.isRefreshing) {
                        _uiState.update { it.copy(content = Resource.Loading) }
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            content = Resource.Error(
                                message = e.message ?: "Ошибка загрузки списка",
                                throwable = e,
                                isCritical = true,
                            ),
                            isRefreshing = false,
                        )
                    }
                }
                .collect { content ->
                    _uiState.update {
                        it.copy(
                            content = Resource.Success(content),
                            isRefreshing = false,
                        )
                    }
                }
        }
    }
}
