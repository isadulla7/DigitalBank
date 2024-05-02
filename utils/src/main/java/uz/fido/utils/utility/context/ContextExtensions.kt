package uz.fido.utils.utility.context

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import uz.fido.utils.R
import uz.fido.utils.log.Logger
import uz.fido.utils.utility.language.Utility.getLocalIpAddress
import uz.fido.utils.view.custom_edit_text.mask_edit_text.MaskEditText
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

@SuppressLint("HardwareIds")
fun Context.getDeviceIds(): String {
    Logger.writeLog("uuid:" + UUID.randomUUID().toString())
    Logger.writeLog("serial:" + Build.SERIAL)
    return Settings.Secure.getString(
        this.contentResolver, Settings.Secure.ANDROID_ID
    )
}

fun Context.checkNetworkStatus(): String {
    var networkStatus = ""
    try {
        val connMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        networkStatus = if (wifi!!.isConnectedOrConnecting) {
            "wifi"
        } else if (mobile!!.isConnectedOrConnecting) {
            "mobileData"
        } else {
            "noNetwork"
        }
    } catch (ex: Exception) {
        Log.e("INFO_ERROR", "NetworkStatus")
    }
    return networkStatus
}

fun Context.wifiIpAddress(): String {
    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var ipAddress = wifiManager.connectionInfo.ipAddress

    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        ipAddress = Integer.reverseBytes(ipAddress)
    }
    val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
    val ipAddressString: String = try {
        InetAddress.getByAddress(ipByteArray).hostAddress ?: ""
    } catch (ex: UnknownHostException) {
        Log.e("INFO_ERROR", "Unable to get host address.")
        ""
    }
    return ipAddressString
}

fun Context.getIpAddress(): String {
    var ipAddress = ""
    try {
        when {
            checkNetworkStatus().equals("wifi", ignoreCase = true) -> {
                ipAddress = wifiIpAddress()
            }

            checkNetworkStatus().equals("mobileData", ignoreCase = true) -> {
                ipAddress = getLocalIpAddress()
            }

            checkNetworkStatus().equals("noNetwork", ignoreCase = true) -> {
                ipAddress = ""
            }
        }
    } catch (ex: Exception) {
        Log.e("INFO_ERROR: ", "IP_Adress" + ex.localizedMessage)
    }
    return ipAddress
}

fun <T> Context.startActivityWithClearTask(activity: Class<T>) {
    val intent = Intent(this, activity)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}

@SuppressLint("SimpleDateFormat")
fun Context.checkForExpireDate(
    expireDate: String, editText: MaskEditText, textInputLayout: TextInputLayout
) {
    try {
        val calendar = Calendar.getInstance()
        val myFormat = "yyyy"
        val sdf = SimpleDateFormat(myFormat)
        var currentYear = sdf.format(calendar.time)
        currentYear = currentYear.substring(2, currentYear.length)
        val year = currentYear.toInt() + 5
        val yearOld = currentYear.toInt() - 5
        if (expireDate.length == 2) {
            if (expireDate.contains("/")) {
                editText.setText("")
                return
            }
            if (expireDate.toInt() > 12 || expireDate.isEmpty()) {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = getString(R.string.wrong_card_expire_date)
                return
            } else {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
        if (expireDate.length == 5) {
            val inputYear = expireDate.substring(3, expireDate.length)
            if (inputYear.toInt() > year || inputYear.toInt() < yearOld) {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = getString(R.string.wrong_card_expire_date)
                return
            } else {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
    } catch (e: NumberFormatException) {
        Log.e("Log", e.toString())
    }


}
