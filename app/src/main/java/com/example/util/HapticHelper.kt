package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

enum class HapticType {
    KEY_TAP,
    OPERATOR_TAP,
    CONFIRM_EQUALS,
    CLEAR_ALL,
    TOGGLE_MODE
}

object HapticHelper {
    fun perform(context: Context, view: View, type: HapticType, enabled: Boolean) {
        if (!enabled) return

        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val effectId = when (type) {
                        HapticType.KEY_TAP -> VibrationEffect.EFFECT_CLICK
                        HapticType.OPERATOR_TAP -> VibrationEffect.EFFECT_HEAVY_CLICK
                        HapticType.CONFIRM_EQUALS -> VibrationEffect.EFFECT_DOUBLE_CLICK
                        HapticType.CLEAR_ALL -> VibrationEffect.EFFECT_HEAVY_CLICK
                        HapticType.TOGGLE_MODE -> VibrationEffect.EFFECT_TICK
                    }
                    vibrator.vibrate(VibrationEffect.createPredefined(effectId))
                    return
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val duration = when (type) {
                        HapticType.KEY_TAP -> 12L
                        HapticType.OPERATOR_TAP -> 22L
                        HapticType.CONFIRM_EQUALS -> 45L
                        HapticType.CLEAR_ALL -> 30L
                        HapticType.TOGGLE_MODE -> 15L
                    }
                    val amplitude = when (type) {
                        HapticType.KEY_TAP -> 140
                        HapticType.CONFIRM_EQUALS -> 255
                        else -> 200
                    }
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                    return
                }
            }
        } catch (_: Exception) {
            // Fallback to view haptic feedback
        }

        val feedbackConstant = when (type) {
            HapticType.CONFIRM_EQUALS -> HapticFeedbackConstants.CONFIRM
            else -> HapticFeedbackConstants.KEYBOARD_TAP
        }
        view.performHapticFeedback(feedbackConstant)
    }
}
