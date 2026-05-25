package com.ecotrack.domain.model

data class UserSettings(
    val userName: String = "Гость",
    val notificationsEnabled: Boolean = true,
    val expiryReminderDays: Int = 3,
    val useDynamicColor: Boolean = true,
    val darkTheme: Boolean? = null,
    val aiPhotoRecognitionEnabled: Boolean = true,
    val aiSmartSuggestionsEnabled: Boolean = true,
    val aiReceiptScanEnabled: Boolean = true,
)
