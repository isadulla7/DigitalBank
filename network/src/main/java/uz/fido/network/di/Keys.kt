package uz.fido.network.di

object Keys {

    init {
        try {
            System.loadLibrary("network-lib")
        } catch (e: UnsatisfiedLinkError) {
            //Failed to load library
        }
    }

    external fun getUserInfoUrl(): String

    external fun getClientId(): String

    external fun getBaseUrl(): String

    external fun getCertificatePin(): String

    external fun getSocketUrl(): String

    external fun paynetPhotoUrl(): String

    external fun getCertFilePassword(): String

    external fun getCertPin1(): String

    external fun getCertPin2(): String

    external fun getCertPin3(): String

    external fun getDomainName(): String
}