package com.ecotrack.domain.usecase.ai

import android.graphics.Bitmap
import com.ecotrack.domain.model.ai.ReceiptScanResult
import com.ecotrack.domain.repository.AiRepository
import javax.inject.Inject

class ParseReceiptUseCase @Inject constructor(
    private val aiRepository: AiRepository,
) {
    suspend operator fun invoke(bitmap: Bitmap): ReceiptScanResult =
        aiRepository.parseReceipt(bitmap)
}
