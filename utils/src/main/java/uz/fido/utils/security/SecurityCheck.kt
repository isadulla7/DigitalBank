package uz.fido.utils.security

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Collections

object SecurityCheck {

    fun isVpnActive(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
    }

    fun isFromVpn(): Boolean {
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

    fun isFromEmulator(): Boolean {
        return (
                Build.FINGERPRINT.startsWith("google/sdk_gphone_")
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

}