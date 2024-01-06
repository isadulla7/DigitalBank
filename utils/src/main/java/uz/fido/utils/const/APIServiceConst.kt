package uz.fido.utils.const

object APIServiceConst {

    //PROFILE IMAGE URL
    fun profileImageUrl(imageName: String) =
        "https://firebasestorage.googleapis.com/v0/b/universal-mobile-digital.appspot.com/o/images%2F$imageName?alt=media&token=e78b872b-fd3c-4dc1-aa12-f59efd22013f&_gl=1*14i0gum*_ga*MTIyNjczNzcxOC4xNjk2MzM2MjI2*_ga_CW55HF8NVT*MTY5NjQwMjk0MC4zLjEuMTY5NjQwMzUwNi4zMS4wLjA."

    //MKB
    const val MKB_URL = "https://mobile.mkb.uz/REST/api/"
    private const val MKB_SOCKET_URL = "https://mobile.mkb.uz/SOCKET/api/"
    private const val MKB_CLIENT_ID = "-2"

    //UNIVERSAL
    const val UNIVERSAL_PAYMENT_PHOTO = "https://ibank.ubank.uz/files/"
    private const val UNIVERSAL_URL = "https://ra.ubank.uz/api/"
    private const val UNIVERSAL_SOCKET_URL = "https://ss.ubank.uz/api"
    private const val UNIVERSAL_CLIENT_ID = "-3"

    //AAB URLS
    const val PAYNET_PHOTO = "https://ibank.ubank.uz/files/"
    private const val AAB_URL = "https://dgb.aab.uz:8443/digb/api/"
    private const val AAB_URL_TEST = "http://10.50.50.166:9595/"
    private const val AAB_SOCKET_URL = "http://my.aab.uz:9191/api/"
    private const val ALLIANCE_CLIENT_ID = "-1"

    //LOCAL
    const val FB_LOCAL_URL = "https://my.fido.uz/dgb_api/"
    const val USER_INFO_URL = "http://ip-api.com/json/"

    //APP CONFIG
    const val BASE_URL = UNIVERSAL_URL
    const val SOCKET_URL = UNIVERSAL_SOCKET_URL
    const val USER_CLIENT_ID = UNIVERSAL_CLIENT_ID

}