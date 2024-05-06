package uz.fido.universaldigital.ui.utils.keys

object Keys {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getMyIdClientId(): String

    external fun getMyIdClientHash(): String

    external fun getMyIdClientHashId(): String

    external fun getUserInfoUrl(): String

    external fun getClientId(): String

    external fun paynetPhotoUrl(): String

    external fun getDepositOfferBaxtliBolalik(): String

    external fun getClientSecret(): String

}