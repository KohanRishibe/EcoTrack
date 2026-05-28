package com.ecotrack.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ecotrack.core.database.dao.ProductDao
import com.ecotrack.core.database.dao.ShoppingItemDao
import com.ecotrack.data.mapper.toDomain
import com.ecotrack.domain.model.UserSettings
import com.ecotrack.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ecotrack_settings",
)

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val shoppingItemDao: ShoppingItemDao,
) : SettingsRepository {

    private val dataStore = context.settingsDataStore

    override fun observeSettings(): Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
            expiryReminderDays = prefs[KEY_EXPIRY_DAYS] ?: 3,
            useDynamicColor = prefs[KEY_DYNAMIC_COLOR] ?: false,
            darkTheme = prefs[KEY_DARK_THEME],
            aiPhotoRecognitionEnabled = prefs[KEY_AI_PHOTO] ?: true,
            aiSmartSuggestionsEnabled = prefs[KEY_AI_SMART] ?: true,
            aiReceiptScanEnabled = prefs[KEY_AI_RECEIPT] ?: true,
        )
    }

    override suspend fun updateSettings(settings: UserSettings) {
        dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS] = settings.notificationsEnabled
            prefs[KEY_EXPIRY_DAYS] = settings.expiryReminderDays
            prefs[KEY_DYNAMIC_COLOR] = settings.useDynamicColor
            prefs[KEY_AI_PHOTO] = settings.aiPhotoRecognitionEnabled
            prefs[KEY_AI_SMART] = settings.aiSmartSuggestionsEnabled
            prefs[KEY_AI_RECEIPT] = settings.aiReceiptScanEnabled
            val darkTheme = settings.darkTheme
            if (darkTheme != null) {
                prefs[KEY_DARK_THEME] = darkTheme
            } else {
                prefs.remove(KEY_DARK_THEME)
            }
        }
    }

    override suspend fun exportData(): String {
        val products = productDao.observeAll().first().map { it.toDomain() }
        val shopping = shoppingItemDao.observeActiveItems().first().map { it.toDomain() }
        val settings = observeSettings().first()
        return buildString {
            appendLine("EcoTrack Export")
            appendLine("Продуктов: ${products.size}")
            products.forEach { p ->
                appendLine("- ${p.name} (${p.category.displayName}), до ${p.expiryDate}")
            }
            appendLine("Список покупок:")
            shopping.forEach { item ->
                appendLine("- [${if (item.isChecked) "x" else " "}] ${item.name}")
            }
        }
    }

    companion object {
        private val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications")
        private val KEY_EXPIRY_DAYS = intPreferencesKey("expiry_days")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        private val KEY_AI_PHOTO = booleanPreferencesKey("ai_photo")
        private val KEY_AI_SMART = booleanPreferencesKey("ai_smart")
        private val KEY_AI_RECEIPT = booleanPreferencesKey("ai_receipt")
    }
}
