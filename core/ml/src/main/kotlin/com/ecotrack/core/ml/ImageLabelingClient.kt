package com.ecotrack.core.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ImageLabel(val text: String, val confidence: Float)

class ImageLabelingClient {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.55f)
            .build(),
    )

    suspend fun analyze(bitmap: Bitmap): List<ImageLabel> = suspendCancellableCoroutine { cont ->
        val image = InputImage.fromBitmap(bitmap, 0)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                cont.resume(
                    labels.map { ImageLabel(it.text, it.confidence) },
                )
            }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    fun close() {
        labeler.close()
    }
}
