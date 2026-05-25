package com.ecotrack.feature.addproduct.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.domain.model.ProductCategory
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onRecognizeByPhoto: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AddProductViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::dismissSnackbar,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Добавить продукт") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.ecoTouchTarget()) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        AddProductScreenContent(
            state = state,
            onNameChange = viewModel::onNameChange,
            onCategoryChange = viewModel::onCategoryChange,
            onQuantityChange = viewModel::onQuantityChange,
            onCategoryExpandedChange = viewModel::onCategoryExpandedChange,
            onShowDatePickerChange = viewModel::onShowDatePickerChange,
            onSave = viewModel::save,
            onExpiryDateChange = viewModel::onExpiryDateChange,
            onRecognizeByPhoto = onRecognizeByPhoto,
            modifier = Modifier.padding(padding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreenContent(
    state: AddProductScreenUiState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (ProductCategory) -> Unit,
    onQuantityChange: (String) -> Unit,
    onCategoryExpandedChange: (Boolean) -> Unit,
    onShowDatePickerChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onExpiryDateChange: (java.time.LocalDate) -> Unit,
    onRecognizeByPhoto: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onRecognizeByPhoto,
            modifier = Modifier.fillMaxWidth().ecoTouchTarget(),
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Text("Распознать по фото (AI)", modifier = Modifier.padding(start = 8.dp))
        }

        state.barcode?.let { barcode ->
            Text(
                text = "Штрихкод: $barcode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth(),
        )

        ExposedDropdownMenuBox(
            expanded = state.categoryExpanded,
            onExpandedChange = onCategoryExpandedChange,
        ) {
            OutlinedTextField(
                value = state.category.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Категория") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(state.categoryExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = state.categoryExpanded,
                onDismissRequest = { onCategoryExpandedChange(false) },
            ) {
                ProductCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            onCategoryChange(category)
                            onCategoryExpandedChange(false)
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.expiryDate.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Срок годности") },
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            onClick = { onShowDatePickerChange(true) },
            modifier = Modifier.ecoTouchTarget(),
        ) {
            Text("Выбрать дату")
        }

        OutlinedTextField(
            value = state.quantity,
            onValueChange = onQuantityChange,
            label = { Text("Количество") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = onSave,
            enabled = !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .ecoTouchTarget(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(if (state.isSaving) "Сохранение…" else "Сохранить")
        }
    }

    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { onShowDatePickerChange(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onExpiryDateChange(date)
                        }
                        onShowDatePickerChange(false)
                    },
                    modifier = Modifier.ecoTouchTarget(),
                ) { Text("OK") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
