package uz.fido.utils.utility.language

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger
import uz.fido.utils.utility.context.startActivityWithClearTask
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Locale
import java.util.regex.Pattern

object Utility {

    fun getDeviceLocale(): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Resources.getSystem().configuration.locale
        }
    }

    fun getDeviceName(): String {
        return Build.MODEL
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
                        return inetAddress.hostAddress!!
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return ""
    }

    fun isValidPasswordFormat(password: String): Boolean {
      //  return password.length >= 8
    val passwordREGEX = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+*/!()_=-])(?=\\S+\$).{4,}\$")
    return passwordREGEX.matcher(password).matches()
    }


}