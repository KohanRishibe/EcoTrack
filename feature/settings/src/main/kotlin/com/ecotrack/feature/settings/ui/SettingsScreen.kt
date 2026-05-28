package com.ecotrack.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.ui.components.EcoResourceContent
import com.ecotrack.core.ui.components.EcoShimmerList
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.domain.model.UserSettings

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPhotoRecognize: () -> Unit = {},
    onOpenReceiptScan: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::dismissSnackbar,
    )

    Scaffold(
        modifier = modifier,
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        SettingsScreenContent(
            state = state,
            onBack = onBack,
            onUpdate = viewModel::update,
            onExport = viewModel::export,
            onOpenPhotoRecognize = onOpenPhotoRecognize,
            onOpenReceiptScan = onOpenReceiptScan,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun SettingsScreenContent(
    state: SettingsScreenUiState,
    onBack: () -> Unit,
    onUpdate: (UserSettings) -> Unit,
    onExport: () -> Unit,
    onOpenPhotoRecognize: () -> Unit,
    onOpenReceiptScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        EcoResourceContent(
            resource = state.settings,
            onRetry = {},
            loading = { EcoShimmerList(itemCount = 3) },
            modifier = Modifier.weight(1f),
        ) { settings ->
            SettingsForm(
                settings = settings,
                onUpdate = onUpdate,
                onOpenPhotoRecognize = onOpenPhotoRecognize,
                onOpenReceiptScan = onOpenReceiptScan,
            )
        }
        Button(
            onClick = onExport,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .ecoTouchTarget(),
        ) {
            Text("Экспорт данных")
        }
        state.exportPreview?.let { preview ->
            Text(
                text = preview,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 8.dp)
                .ecoTouchTarget(),
        ) {
            Text("Назад")
        }
    }
}

@Composable
private fun SettingsForm(
    settings: UserSettings,
    onUpdate: (UserSettings) -> Unit,
    onOpenPhotoRecognize: () -> Unit,
    onOpenReceiptScan: () -> Unit,
) {
    Column {
        SettingsSwitchRow(
            label = "Уведомления",
            checked = settings.notificationsEnabled,
            onChecked = { onUpdate(settings.copy(notificationsEnabled = it)) },
        )
        SettingsSwitchRow(
            label = "Динамический цвет (Material You)",
            checked = settings.useDynamicColor,
            onChecked = { onUpdate(settings.copy(useDynamicColor = it)) },
        )
        SettingsSwitchRow(
            label = "Тёмная тема",
            checked = settings.darkTheme ?: false,
            onChecked = { onUpdate(settings.copy(darkTheme = it)) },
        )
        Text(
            "AI-функции",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        SettingsSwitchRow(
            label = "Распознавание по фото (ML Kit)",
            checked = settings.aiPhotoRecognitionEnabled,
            onChecked = { onUpdate(settings.copy(aiPhotoRecognitionEnabled = it)) },
        )
        SettingsSwitchRow(
            label = "Умные подсказки покупок",
            checked = settings.aiSmartSuggestionsEnabled,
            onChecked = { onUpdate(settings.copy(aiSmartSuggestionsEnabled = it)) },
        )
        SettingsSwitchRow(
            label = "Сканирование чеков (OCR)",
            checked = settings.aiReceiptScanEnabled,
            onChecked = { onUpdate(settings.copy(aiReceiptScanEnabled = it)) },
        )
        Button(
            onClick = onOpenPhotoRecognize,
            enabled = settings.aiPhotoRecognitionEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .ecoTouchTarget(),
        ) {
            Text("Открыть распознавание по фото")
        }
        Button(
            onClick = onOpenReceiptScan,
            enabled = settings.aiReceiptScanEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .ecoTouchTarget(),
        ) {
            Text("Открыть сканер чеков")
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .ecoTouchTarget(),
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChecked,
        )
    }
}
