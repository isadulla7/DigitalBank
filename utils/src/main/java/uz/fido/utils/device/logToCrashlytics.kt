package uz.fido.utils.device

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.google.firebase.analytics.FirebaseAnalytics

@RequiresPermission(Manifest.permission.WAKE_LOCK)
fun logToCrashlytics(tag: String, message: String, context: Context) {
    try {
        val analytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(tag, message)
            putString("userDevice", android.os.Build.MODEL)
        }
        analytics.logEvent("emulator_check", bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@RequiresPermission(Manifest.permission.WAKE_LOCK)
fun logRootToCrashlytics(tag: String, message: String, context: Context) {
    try {
        val analytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle().apply {
            putString(tag, message)
            putString("userDevice", android.os.Build.MODEL)
        }
        analytics.logEvent("root_check", bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}