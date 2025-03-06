package uz.fido.utils.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.net.NetworkInterface

open class EmulatorCheck(private val context: Context) {

    fun isProbablyAnEmulator(): Boolean {
        return checkDockerEnvironment()
                || isEmulator()
                || checkForEmulatorFiles()
                || checkEmulatorSoftware()
    }

    private fun checkForEmulatorFiles(): Boolean {
        val knownFiles = arrayOf(
            "/dev/qemu_pipe",
            "/dev/qemud",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        )
        return knownFiles.any { File(it).exists() }
    }

    private fun checkEmulatorSoftware(): Boolean {
        val knownEmulatorPackages = arrayOf(
            "com.bluestacks",
            "com.bignox.app",
            "com.bluestacks.home",
            "com.koplayer"
        )
        val pm = context.packageManager
        return knownEmulatorPackages.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun isEmulator(): Boolean {
        return (
                (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
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
                        || Build.PRODUCT == "google_sdk"
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
                        || Build.PRODUCT == "google_sdk")
                || checkForMEmu()
                || checkForLDPlayer()
                || checkForNoxPlayer()
                || checkForGenymotion()
                || checkForKoPlayer()
    }


    private fun checkForMEmu(): Boolean {
        val productModel = Build.MODEL
        return productModel.contains("MEmu")
    }

    private fun checkForLDPlayer(): Boolean {
        val productModel = Build.MODEL
        return productModel.contains("LDPlayer")
    }

    private fun checkForNoxPlayer(): Boolean {
        val knownPackages = arrayOf("com.noxgroup.app")
        val pm = context.packageManager
        return knownPackages.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun checkForGenymotion(): Boolean {
        val model = Build.MODEL
        return model.contains("Genymotion") || model.contains("google_sdk")
    }

    private fun checkForKoPlayer(): Boolean {
        val knownPackages = arrayOf("com.koplayer.app")
        val pm = context.packageManager
        return knownPackages.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun checkDockerEnvironment(): Boolean {
        val dockerEnvFile = File("/.dockerenv")
        if (dockerEnvFile.exists()) {
            return true
        }

        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces().asSequence().toList()
            interfaces.any { networkInterface ->
                networkInterface.name.startsWith("eth") && networkInterface.name != "eth0"
            }
        } catch (e: Exception) {
            false
        }
    }
}