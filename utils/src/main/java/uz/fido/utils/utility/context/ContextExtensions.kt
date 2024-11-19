package uz.fido.utils.utility.context

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import uz.fido.utils.R
import uz.fido.utils.log.Logger
import uz.fido.utils.utility.language.Utility.getLocalIpAddress
import uz.fido.utils.view.custom_edit_text.mask_edit_text.MaskEditText
import java.net.Inet4Address
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
    val networkStatus = "noNetwork"
    try {
        val connMgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connMgr.activeNetwork
        val activeNetwork = connMgr.getNetworkCapabilities(nw)
        return if (activeNetwork != null) {
            when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobileData"
                //for other device how are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
                //for check internet over Bluetooth
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "bluetooth"
                else -> "noNetwork"
            }
        } else "noNetwork"
    } catch (ex: Exception) {
        Log.e("INFO_ERROR", "NetworkStatus")
    }
    return networkStatus
}

fun Context.wifiIpAddress(): String {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: Network? = connectivityManager.activeNetwork
    val networkCapabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(activeNetwork)
    if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
        val linkProperties: LinkProperties? = connectivityManager.getLinkProperties(activeNetwork)
        linkProperties?.let {
            for (address in it.linkAddresses) {
                val inetAddress = address.address
                if (inetAddress is Inet4Address) {
                    return inetAddress.hostAddress ?: "1.1.1.1"
                }
            }
        }
    }
    return "1.1.1.1"
}

fun Context.getIpAddress(): String {
    var ipAddress = "1.1.1.1"
    try {
        ipAddress = when {
            checkNetworkStatus().equals("wifi", ignoreCase = true) -> {
                wifiIpAddress()
            }

            checkNetworkStatus().equals("mobileData", ignoreCase = true) -> {
                getLocalIpAddress()
            }

            else -> {
                "1.1.1.1"
            }
        }
    } catch (ex: Exception) {
        Log.e("INFO ERROR: ", "IP-address" + ex.localizedMessage)
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
