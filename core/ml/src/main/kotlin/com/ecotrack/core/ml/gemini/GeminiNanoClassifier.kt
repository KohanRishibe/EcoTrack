package com.ecotrack.core.ml.gemini

import android.graphics.Bitmap

/**
 * Заглушка для интеграции Gemini Nano (AICore) на поддерживаемых устройствах.
 * Сейчас возвращает null — используется ML Kit Image Labeling как fallback.
 *
 * Для продакшена: подключить `com.google.ai.edge.aicore` и реализовать
 * generative vision prompt для категории и срока хранения.
 */
interface GeminiNanoClassifier {
    fun isAvailable(): Boolean
    suspend fun suggestProductFromPhoto(bitmap: Bitmap): GeminiProductSuggestion?
}

data class GeminiProductSuggestion(
    val productName: String,
    val categoryHint: String,
    val shelfLifeDays: Int,
)

class NoOpGeminiNanoClassifier : GeminiNanoClassifier {
    override fun isAvailable(): Boolean = false
    override suspend fun suggestProductFromPhoto(bitmap: Bitmap): GeminiProductSuggestion? = null
}
