package uz.fido.utils.utility.context

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import uz.fido.utils.log.Logger
import java.math.BigInteger
import java.net.*
import java.nio.ByteOrder
import java.util.*

class GetDeviceInfo(var context: Context) {

    class DeviceInfo {
        var sim_iccd: String? = null
        var network_state: String? = null
        var imei_data: String? = null
        var os_system_version_api: String? = null
    }

    //для получения готового JsonObject
    val deviceInfo: DeviceInfo
        get() {
            val deviceInfo = DeviceInfo()
            deviceInfo.imei_data = iMEIs.toString()
            deviceInfo.sim_iccd = simICCDs.toString()
            deviceInfo.network_state = checkNetworkStatus(context)
            deviceInfo.os_system_version_api = "A"
            return deviceInfo
        }

    // Получить серийные номера сим карт, сколько симок столько же серийных номеров,
    // при неполадке возвращает пустой лист
    private val simICCDs: ArrayList<String>
        get() {
            val simSerialArray = ArrayList<String>()
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        val subsManager: SubscriptionManager?
                        subsManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                        Logger.writeLog("theree")
                        val subsList = subsManager.activeSubscriptionInfoList
                        if (subsList != null) {
                            for (subsInfo in subsList) {
                                if (subsInfo != null) {
                                    val simSerialNo = subsInfo.iccId
                                    simSerialArray.add(simSerialNo)
                                    Logger.writeLog("SImSerialNUMBER: $simSerialNo")
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("INFO_ERROR: ", "SimICCD")
            }
            return simSerialArray
        }

    //Получить IMEI адреса устройства, так как в двух симочных две IMEI,
    // если API>28 возвращает пустой лист
    private val iMEIs: ArrayList<String>
        get() {
            val imeiList = ArrayList<String>()
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    if (null != tm) {
                        for (i in 0 until tm.phoneCount) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (tm.getImei(i) != null && tm.getImei(i).isNotEmpty()) {
                                    imeiList.add(tm.getImei(i))
                                } else {
//                                    imeiList.add(null);
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

    private fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return ""
    }

    private fun wifiIpAddress(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        val ipAddressString: String? = try {
            InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            Log.e("INFO_ERROR", "Unable to get host address.")
            null
        }
        return ipAddressString
    }

    private fun checkNetworkStatus(context: Context): String {
        var networkStatus = ""
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            }
        } catch (ex: Exception) {
            Log.e("INFO_ERROR", "NetworkStatus")
        }
        return networkStatus
    }

}