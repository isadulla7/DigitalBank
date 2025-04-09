package uz.fido.utils.security

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import com.scottyab.rootbeer.RootBeer
import uz.fido.utils.device.logRootToCrashlytics
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Method
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

    fun isFromEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT == "google_sdk"
                || SystemProperties.getProp("ro.kernel.qemu") == "1"
    }

    fun Activity.isRunningOnEmulator(): Boolean = EmulatorCheck(this).isProbablyAnEmulator()

    fun Activity.isPhoneRooted(): Boolean {
        return RootBeer(this).isRooted || checkRootedFiles(this) || checkSuExists(this) || checkBuildTags(this)
    }

    private fun checkSuExists(context: Context): Boolean {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val result = bufferedReader.readLine() != null
            if (result) {
                logRootToCrashlytics("checkSuExists", bufferedReader.readLine().toString(), context)
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            return false
        }
    }

    private fun checkRootedFiles(context: Context): Boolean {
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
            if (File(path).exists()) {
                logRootToCrashlytics("checkRootedFiles", path, context)
                return true
            }
        }
        return false
    }

    private fun checkBuildTags(context: Context): Boolean {
        val buildTags = Build.TAGS
        val result = buildTags != null && buildTags.contains("test-keys")
        if (result) {
            logRootToCrashlytics("checkBuildTags", buildTags, context)
            return true
        } else {
            return false
        }
    }

    object SystemProperties {
        private var failedUsingReflection = false
        private var getPropMethod: Method? = null

        @SuppressLint("PrivateApi")
        fun getProp(propName: String, defaultResult: String = ""): String {
            if (!failedUsingReflection) try {
                if (getPropMethod == null) {
                    val clazz = Class.forName("android.os.SystemProperties")
                    getPropMethod = clazz.getMethod("get", String::class.java, String::class.java)
                }
                return getPropMethod!!.invoke(null, propName, defaultResult) as String? ?: defaultResult
            } catch (e: Exception) {
                getPropMethod = null
                failedUsingReflection = true
            }
            var process: Process? = null
            try {
                process = Runtime.getRuntime().exec("getprop \"$propName\" \"$defaultResult\"")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                return reader.readLine()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                process?.destroy()
            }
            return defaultResult
        }
    }

}