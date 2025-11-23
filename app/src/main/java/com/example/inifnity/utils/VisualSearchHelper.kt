package com.example.inifnity.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object VisualSearchHelper {

    suspend fun analyzeImage(bitmap: Bitmap): String? {
        return suspendCancellableCoroutine { continuation ->
            try {

                val image = InputImage.fromBitmap(bitmap, 0)


                val options = ImageLabelerOptions.Builder()
                    .setConfidenceThreshold(0.3f)
                    .build()

                val labeler = ImageLabeling.getClient(options)


                labeler.process(image)
                    .addOnSuccessListener { labels ->

                        val bestLabel = labels.maxByOrNull { it.confidence }?.text

                        if (!bestLabel.isNullOrEmpty()) {
                            continuation.resume(bestLabel)
                        } else {
                            continuation.resume("Abstract")
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        continuation.resume(null)
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }
}