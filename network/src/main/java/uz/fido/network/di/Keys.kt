package uz.fido.network.di

object Keys {

    init {
        System.loadLibrary("network-lib")
    }

    external fun getUserInfoUrl(): String

    external fun getClientId(): String

    external fun getBaseUrl(): String

    external fun getSocketUrl(): String

    external fun paynetPhotoUrl(): String
}