package com.ecotrack.domain.model

data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val expiryReminderDays: Int = 3,
    val useDynamicColor: Boolean = false,
    val darkTheme: Boolean? = null,
    val aiPhotoRecognitionEnabled: Boolean = true,
    val aiSmartSuggestionsEnabled: Boolean = true,
    val aiReceiptScanEnabled: Boolean = true,
)
