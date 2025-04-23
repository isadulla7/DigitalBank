package uz.fido.utils.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import uz.fido.utils.device.logToCrashlytics
import uz.fido.utils.utility.language.Utility
import java.io.File

open class EmulatorCheck(private val context: Context) {

    fun isProbablyAnEmulator(): Boolean {
        return isEmulator() ||
                checkForEmulatorFiles() ||
                checkEmulatorSoftware() ||
                checkForMEmu() ||
                checkForLDPlayer() ||
                checkForNoxPlayer() ||
                checkForGenymotion() ||
                checkForKoPlayer()
    }

    private fun checkForEmulatorFiles(): Boolean {
        val knownFiles = arrayOf(
            "/dev/qemu_pipe",
            "/dev/qemud",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        )
        val result = knownFiles.firstOrNull { File(it).exists() }
        if (result != null) tryToLogCrashlytics(result)
        return knownFiles.any { File(it).exists() }
    }

    private fun checkEmulatorSoftware(): Boolean {
        val knownEmulatorPackages = arrayOf(
            "com.bluestacks",
            "com.bignox.app",
            "com.noxgroup.app",
            "com.bluestacks.home",
            "com.koplayer"
        )
        val pm = context.packageManager
        return knownEmulatorPackages.any { packageName ->
            try {
                val result = pm.getPackageInfo(packageName, 0)
                tryToLogCrashlytics(result.packageName)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun isEmulator(): Boolean {
        val result = (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google"
                && Build.PRODUCT.startsWith("sdk_gphone_")
                && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.HOST == "Build2"
                || Build.BRAND == "generic"
                || Build.HARDWARE == "goldfish"
                || Build.HARDWARE == "ranchu"
                || Build.HARDWARE == "vbox86"
                || Build.PRODUCT == "sdk"
                || Build.PRODUCT == "sdk_x86"
                || Build.PRODUCT == "sdk_google"
                || Build.PRODUCT == "Andy"
                || Build.PRODUCT == "Droid4X"
                || Build.PRODUCT == "nox"
                || Build.PRODUCT == "vbox86p"
                || Build.MANUFACTURER.contains("Andy")
                || Build.MANUFACTURER.contains("Bluestacks")
                || Build.MANUFACTURER.contains("BigNox")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BOARD == "QC_Reference_Phone"
                || Build.HOST.startsWith("Build")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.PRODUCT == "google_sdk"
        if (result) tryToLogCrashlytics("isEmulator")
        return result
    }

    private fun checkForMEmu(): Boolean {
        val productModel = Build.MODEL
        val result = productModel.contains("MEmu")
        if (result) tryToLogCrashlytics("MEmu")
        return result
    }

    private fun checkForLDPlayer(): Boolean {
        val productModel = Build.MODEL
        val result = productModel.contains("LDPlayer")
        if (result) tryToLogCrashlytics("LDPlayer")
        return result
    }

    private fun checkForNoxPlayer(): Boolean {
        val knownPackages = arrayOf("com.noxgroup.app")
        val pm = context.packageManager
        return knownPackages.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                tryToLogCrashlytics("com.noxgroup.app")
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun checkForGenymotion(): Boolean {
        val model = Build.MODEL
        val result = model.contains("Genymotion") || model.contains("google_sdk")
        if (result) tryToLogCrashlytics("Genymotion or google_sdk")
        return result
    }

    private fun checkForKoPlayer(): Boolean {
        val knownPackages = arrayOf("com.koplayer.app")
        val pm = context.packageManager
        return knownPackages.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                tryToLogCrashlytics("checkForKoPlayer")
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun tryToLogCrashlytics(message: String? = null) {
        try {
            logToCrashlytics("Emulator Check", Utility.getDeviceName() + "failed function: " + message.orEmpty(), context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}