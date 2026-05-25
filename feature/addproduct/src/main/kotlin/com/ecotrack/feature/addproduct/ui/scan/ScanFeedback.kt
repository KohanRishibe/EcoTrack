package com.ecotrack.feature.addproduct.ui.scan

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class ScanFeedback(private val context: Context) {

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)

    fun onScanSuccess() {
        vibrate()
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(120)
        }
    }

    fun release() {
        toneGenerator.release()
    }
}

@Composable
fun rememberScanFeedback(): ScanFeedback {
    val context = LocalContext.current
    val feedback = remember { ScanFeedback(context.applicationContext) }
    DisposableEffect(Unit) {
        onDispose { feedback.release() }
    }
    return feedback
}
