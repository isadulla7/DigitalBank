package uz.fido.utils.security

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import uz.fido.utils.utility.context.getIpAddress
import java.net.NetworkInterface


object NetworkInterfaceExample {
    @JvmStatic
    fun main(context: Activity) {

        var counter = 1
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()

        //1st method (NetworkInterface)
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            println("$counter. Name: " + networkInterface.name)
            println("Display Name: " + networkInterface.displayName)
            println("Is currently available? : " + networkInterface.isUp)
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val address = inetAddresses.nextElement()
                println("\tInetAddress: $address")
                println("\tInetHostAddress: " + address.hostAddress)
            }
            println("---------------------------------------------------------------------")
            counter++
        }

        //2nd method (ConnectivityManager)
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        println("ipAddress: " + context.getIpAddress())
        println("activeNetwork: " + connectivityManager.activeNetwork)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            println("transportInfo: ${activeNetwork?.transportInfo}")
            println("networkSpecifier: ${activeNetwork?.networkSpecifier}")
        }
        println("hasTransportVPN: " + activeNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
    }
}
