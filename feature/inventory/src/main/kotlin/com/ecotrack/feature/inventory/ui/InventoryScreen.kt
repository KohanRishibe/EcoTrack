package com.ecotrack.feature.inventory.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.design.theme.ExpiryCritical
import com.ecotrack.core.ui.components.EcoResourceContent
import com.ecotrack.core.ui.components.EcoShimmerList
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.feature.inventory.domain.InventoryGrouped
import com.ecotrack.feature.inventory.ui.components.InventoryCategoryHeader
import com.ecotrack.feature.inventory.ui.components.InventoryProductRow
import com.ecotrack.feature.inventory.ui.components.InventoryScreenHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onProductClick: (Long) -> Unit,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    InventoryScreenContent(
        state = state,
        onProductClick = onProductClick,
        onAddProduct = onAddProduct,
        onRefresh = viewModel::refresh,
        onDeleteProduct = viewModel::onDeleteProduct,
        onUndoDelete = viewModel::undoDelete,
        onDismissSnackbar = viewModel::dismissSnackbar,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InventoryScreenContent(
    state: InventoryScreenUiState,
    onProductClick: (Long) -> Unit,
    onAddProduct: () -> Unit,
    onRefresh: () -> Unit,
    onDeleteProduct: (InventoryItemUi, com.ecotrack.domain.model.Product) -> Unit,
    onUndoDelete: () -> Unit,
    onDismissSnackbar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = onDismissSnackbar,
        onAction = onUndoDelete,
    )

    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            EcoResourceContent(
                resource = state.content,
                onRetry = onRefresh,
                emptyMessage = "Запасы пусты — нажмите «Добавить» и внесите первый продукт.",
                isEmpty = { it.groups.isEmpty() },
                loading = { EcoShimmerList() },
            ) { grouped ->
                InventoryList(
                    grouped = grouped,
                    onProductClick = onProductClick,
                    onAddProduct = onAddProduct,
                    onDeleteProduct = onDeleteProduct,
                )
            }
        }
        EcoSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InventoryList(
    grouped: InventoryGrouped,
    onProductClick: (Long) -> Unit,
    onAddProduct: () -> Unit,
    onDeleteProduct: (InventoryItemUi, com.ecotrack.domain.model.Product) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            InventoryScreenHeader(
                grouped = grouped,
                onAddProduct = onAddProduct,
            )
        }
        grouped.groups.forEach { (category, items) ->
            stickyHeader {
                InventoryCategoryHeader(
                    category = category,
                    itemCount = items.size,
                )
            }
            items(items, key = { it.id }) { item ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(),
                ) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                grouped.productsById[item.id]?.let { product ->
                                    onDeleteProduct(item, product)
                                }
                                true
                            } else {
                                false
                            }
                        },
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 5.dp)
                                    .background(
                                        ExpiryCritical.copy(alpha = 0.85f),
                                        MaterialTheme.shapes.medium,
                                    ),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                androidx.compose.material3.Text(
                                    text = "Удалить",
                                    color = Color.White,
                                    modifier = Modifier.padding(end = 24.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        content = {
                            InventoryProductRow(
                                item = item,
                                onClick = { onProductClick(item.id) },
                            )
                        },
                    )
                }
            }
        }
    }
}
