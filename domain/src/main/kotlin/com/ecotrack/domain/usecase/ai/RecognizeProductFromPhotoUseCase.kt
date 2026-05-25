package com.ecotrack.domain.usecase.ai

import android.graphics.Bitmap
import com.ecotrack.domain.model.ai.ProductPhotoInsight
import com.ecotrack.domain.repository.AiRepository
import javax.inject.Inject

class RecognizeProductFromPhotoUseCase @Inject constructor(
    private val aiRepository: AiRepository,
) {
    suspend operator fun invoke(bitmap: Bitmap): ProductPhotoInsight? =
        aiRepository.recognizeProductFromPhoto(bitmap)
}
