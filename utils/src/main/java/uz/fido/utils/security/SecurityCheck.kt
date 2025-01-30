package uz.fido.utils.security

import android.os.Build
import java.io.File
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections

object SecurityCheck {

    fun isVpnActive(): Boolean {
        var interfaceName = ""
        try {
            for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp) interfaceName = networkInterface.name
                if (
                    interfaceName.contains("tun") ||
                    interfaceName.contains("ppp") ||
                    interfaceName.contains("pptp")
                ) {
                    return true
                }
            }
        } catch (exception: SocketException) {
            exception.printStackTrace()
        }
        return false
    }

    fun isRunningOnEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google"
                && Build.PRODUCT.startsWith("sdk_gphone_")
                && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_")
                ) || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT == "google_sdk"
    }

    fun isPhoneRooted(): Boolean {
        return (canExecuteSu() || isMagiskPresent() || canWriteToSystem() || checkRootProps())
    }

    private fun canExecuteSu(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "whoami"))
            val output = process.inputStream.bufferedReader().readLine()
            output != null && output.contains("root")
        } catch (e: Exception) {
            false
        }
    }

    private fun isMagiskPresent(): Boolean {
        try {
            val paths = listOf(
                "/sbin/.magisk",
                "/cache/.disable_magisk",
                "/system/etc/init/magisk.rc"
            )
            for (path in paths) {
                if (File(path).exists()) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    private fun canWriteToSystem(): Boolean {
        return try {
            val file = File("/system/test_root_check")
            val success = file.createNewFile()
            if (success) file.delete()
            success
        } catch (e: Exception) {
            false
        }
    }

    private fun checkRootProps(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("getprop ro.build.tags")
            val output = process.inputStream.bufferedReader().readLine()
            output != null && output.contains("test-keys")
        } catch (e: Exception) {
            false
        }
    }

}