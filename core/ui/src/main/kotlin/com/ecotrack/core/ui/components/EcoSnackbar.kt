package com.ecotrack.core.ui.components

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

data class SnackbarMessage(
    val text: String,
    val actionLabel: String? = null,
)

@Composable
fun EcoSnackbarEffect(
    message: SnackbarMessage?,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onAction: () -> Unit = {},
) {
    LaunchedEffect(message) {
        message ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = message.text,
            actionLabel = message.actionLabel,
        )
        if (result == SnackbarResult.ActionPerformed) {
            onAction()
        }
        onDismiss()
    }
}

@Composable
fun EcoSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier)
}
