package uz.fido.utils.security

import android.app.Activity
import com.google.firebase.crashlytics.internal.common.CommonUtils
import com.scottyab.rootbeer.RootBeer
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
                if (interfaceName.contains("tun") || interfaceName.contains("ppp") || interfaceName.contains("pptp")) {
                    return true
                }
            }
        } catch (exception: SocketException) {
            exception.printStackTrace()
        }
        return false
    }

    fun Activity.isRunningOnEmulator(): Boolean = EmulatorCheck(this).isProbablyAnEmulator()

    fun Activity.isPhoneRooted(): Boolean {
        return checkRootedFiles() || canExecuteSu() || isMagiskPresent() || canWriteToSystem() || checkRootProps() || CommonUtils.isRooted(this) || RootBeer(this).isRooted
    }

    private fun checkRootedFiles(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
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