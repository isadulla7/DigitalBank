package uz.fido.network.di

import uz.fido.utils.log.Log

object Keys {

    init {
        try {
            System.loadLibrary("network-lib")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("NativeLibrary", "Failed to load library: ${e.message}")
        }
    }

    external fun getUserInfoUrl(): String

    external fun getClientId(): String

    external fun getBaseUrl(): String

    external fun getSocketUrl(): String

    external fun paynetPhotoUrl(): String
}