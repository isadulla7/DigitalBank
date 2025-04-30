package uz.fido.utils.utility.language

import android.os.Build
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.regex.Pattern

object Utility {

    fun getDeviceName(): String {
        return Build.MODEL ?: ""
    }

    fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress ?: "1.1.1.1"
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return "1.1.1.1"
    }

    fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+*/!()_=-?])(?=\\S+\$).{4,}\$")
        return passwordREGEX.matcher(password).matches()
    }

    private fun String.containsNumber(): Boolean {
        val regex = "\\d+".toRegex()
        return regex.containsMatchIn(this)
    }

    fun passwordIsValid(password: String): Boolean {
        return password.containsNumber() && password.length in 8..15 && password.hasLetter() && password.hasSpecialSymbol()
    }

    private fun String.hasLetter(): Boolean {
        val uppercaseRegex = Regex("[A-Z]")
        val lowercaseRegex = Regex("[a-z]")
        val hasUpperCase = uppercaseRegex.containsMatchIn(this)
        val hasLowerCase = lowercaseRegex.containsMatchIn(this)
        return hasUpperCase && hasLowerCase
    }

    private fun String.hasSpecialSymbol(): Boolean {
        val regex = Regex("[^A-Za-z0-9 ]")
        return regex.containsMatchIn(this)
    }

}