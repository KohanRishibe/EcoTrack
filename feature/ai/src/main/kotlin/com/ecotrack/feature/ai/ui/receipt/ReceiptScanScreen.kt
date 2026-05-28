package com.ecotrack.feature.ai.ui.receipt

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.domain.model.ai.ReceiptLineItem
import com.ecotrack.feature.ai.ui.photo.PhotoCapturePreview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReceiptScanScreen(
    onBack: () -> Unit,
    onImportComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptScanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var captureTrigger by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.importDone) {
        if (state.importDone) onImportComplete()
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
                title = { Text("Сканировать чек") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.ecoTouchTarget()) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val scan = state.scanResult) {
            is Resource.Success -> {
                ReceiptItemsContent(
                    items = scan.data.items,
                    selectedIndices = state.selectedIndices,
                    rawPreview = scan.data.rawText.take(200),
                    isImporting = state.isImporting,
                    onToggle = viewModel::toggleItem,
                    onImport = viewModel::importSelected,
                    onRescan = {
                        viewModel.reset()
                        captureTrigger = 0
                    },
                    modifier = Modifier.padding(padding),
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    if (!cameraPermission.status.isGranted) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                                Text("Разрешить камеру")
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f)) {
                            PhotoCapturePreview(
                                onPhotoCaptured = viewModel::onReceiptPhotoCaptured,
                                captureTrigger = captureTrigger,
                                modifier = Modifier.fillMaxSize(),
                            )
                            if (state.isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                        Button(
                            onClick = { captureTrigger++ },
                            enabled = !state.isProcessing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .ecoTouchTarget(),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Сфотографировать чек", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    if (scan is Resource.Error) {
                        Text(
                            scan.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptItemsContent(
    items: List<ReceiptLineItem>,
    selectedIndices: Set<Int>,
    rawPreview: String,
    isImporting: Boolean,
    onToggle: (Int) -> Unit,
    onImport: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("Найдено позиций: ${items.size}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "ML Kit Text Recognition",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        if (items.isEmpty()) {
            Text(
                "Позиции не распознаны. Попробуйте снять чек при хорошем освещении.",
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Button(onClick = onRescan) { Text("Снять снова") }
            return
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(items) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .ecoTouchTarget(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = index in selectedIndices,
                        onCheckedChange = { onToggle(index) },
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        val details = buildList {
                            item.quantity?.let { add("$it ${item.unit ?: "шт"}") }
                            item.price?.let { add("${it} ₽") }
                        }
                        if (details.isNotEmpty()) {
                            Text(details.joinToString(" · "), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        Text(
            text = rawPreview,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Button(
            onClick = onImport,
            enabled = !isImporting && selectedIndices.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isImporting) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Добавить выбранное (${selectedIndices.size})")
        }
        Button(onClick = onRescan, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Снять чек заново")
        }
    }
}
