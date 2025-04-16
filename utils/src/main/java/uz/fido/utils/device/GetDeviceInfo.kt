package uz.fido.utils.device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat

class GetDeviceInfo(var context: Context) {

    class DeviceInfo {
        var simCcd: String? = null
        var networkState: String? = null
        var imeiData: String? = null
        var osSystemVersionApi: String? = null
    }

    val deviceInfo: DeviceInfo
        get() {
            val deviceInfo = DeviceInfo()
            deviceInfo.imeiData = iMEIs.toString()
            deviceInfo.simCcd = simICCDs.toString()
            deviceInfo.networkState = checkNetworkStatus(context)
            deviceInfo.osSystemVersionApi = "A"
            return deviceInfo
        }

    private val simICCDs: ArrayList<String>
        get() {
            val simSerialArray = ArrayList<String>()
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val subsManager: SubscriptionManager?
                    subsManager =
                        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                    val subsList = subsManager.activeSubscriptionInfoList
                    if (subsList != null) {
                        for (subsInfo in subsList) {
                            if (subsInfo != null) {
                                val simSerialNo = subsInfo.iccId
                                simSerialArray.add(simSerialNo)
                            }
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e("INFO_ERROR: ", "SimICCD:$exception")
            }
            return simSerialArray
        }

    private val iMEIs: ArrayList<String>
        get() {
            val imeiList = ArrayList<String>()
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                ) {
                    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    if (null != tm) {
                        for (i in 0 until tm.phoneCount) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (tm.getImei(i) != null && tm.getImei(i).isNotEmpty()) {
                                    imeiList.add(tm.getImei(i))
                                }
                            } else {
                                imeiList.add(tm.deviceId)
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("INFO_ERROR: ", "IMEIs")
            }
            return imeiList
        }

    private fun checkNetworkStatus(context: Context): String {
        var networkStatus = ""
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            networkStatus = if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        "wifi"
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        "mobileData"
                    }

                    else -> {
                        "noNetwork"
                    }
                }
            } else {
                "noNetwork"
            }
        } catch (exception: Exception) {
            Log.e("INFO_ERROR", "NetworkStatus:$exception")
        }
        return networkStatus
    }

}