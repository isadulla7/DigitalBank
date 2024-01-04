package uz.fido.utils.device

import android.content.Context
import androidx.biometric.BiometricManager

fun isFingerEnable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate()) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            false
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            false
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            false
        }

        else -> {
            true
        }
    }
}