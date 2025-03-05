package uz.fido.utils.internet_checker

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData

/**
 * Created by Husniddin Muhammad Amin on 17.01.2023
 * Tashkent, Uzbekistan.
 */

class InternetConnectionChecker(context: Context) : LiveData<Boolean>() {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun onActive() {
        super.onActive()
        try {
            connectivityManager.registerDefaultNetworkCallback(getNetworkCallback())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInactive() {
        super.onInactive()
        try {
            connectivityManager.unregisterNetworkCallback(getNetworkCallback())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }
    }

}