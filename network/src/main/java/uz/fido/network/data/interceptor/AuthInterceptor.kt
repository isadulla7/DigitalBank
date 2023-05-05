//package uz.fido.network.data.interceptor
//
//import android.content.Context
//import android.os.Build
//import android.widget.Toast
//import io.paperdb.Paper
//import okhttp3.Interceptor
//import okhttp3.Request
//import okhttp3.Response
//import uz.fido.utils.security.DiffieHellman
//import java.util.*
//import javax.inject.Inject
//import kotlin.math.abs
//
///**
// * Created by Husniddin Muhammad Amin on 02.05.2023
// * Tashkent, Uzbekistan.
// */
//
//class AuthInterceptor @Inject constructor(
//    private val swapKeyService: SwapKeyService, private val context: Context, private val apiInterface: dagger.Lazy<ApiInterface>
//) : Interceptor {
//
//    private lateinit var lastSwapKeyCallTime: Date
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//        val originalResponse = chain.proceed(originalRequest)
//        if (originalResponse.code != TOKEN_EXPIRED && originalResponse.code != UNAUTHORIZED) {
//            return originalResponse
//        }
//        synchronized(this) {
//            if (isNeedToCallSwapKey()) {
//                lastSwapKeyCallTime = Calendar.getInstance().time
//                val swapKeysResponse = getSwapKeyResponse()
//                if (swapKeysResponse.code() == 200) {
//                    setKeyForDiffieHellman(swapKeysResponse.body())
//                    val ipResponse = getIpResponse()
//                    if (ipResponse.code() == 200) {
//                        ipResponse.body()?.let { userInfo ->
//                            val signInResponse = getSignInResponse(userInfo)
//                            if (signInResponse.code() == 200) {
//                                signInResponse.body()?.let {
//                                    saveSignInPinResponse(it)
//                                    returnModifiedRequest(originalRequest, chain)
//                                }
//                            }
//                        }
//                    } else {
//                        Toast.makeText(context, ipResponse.message(), Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(context, swapKeysResponse.message(), Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                returnModifiedRequest(originalRequest, chain)
//            }
//        }
//        return originalResponse
//    }
//
//    private fun returnModifiedRequest(originalRequest: Request, chain: Interceptor.Chain): Response {
//        val modifiedRequest = originalRequest.newBuilder().header("Authorization", getClientToken()).build()
//        return chain.proceed(modifiedRequest)
//    }
//
//    private fun getSwapKeyResponse(): retrofit2.Response<SwapKeysResponse> {
//        return swapKeyService.swapKey(
//            SwapKeysRequest(
//                device_code = context.getDeviceId(),
//                public_key1 = DiffieHellman.getDiffieHellman()._g.toBigInteger(),
//                public_key2 = DiffieHellman.getDiffieHellman()._p.toBigInteger(),
//                encryptData = DiffieHellman.getDiffieHellman().keyA
//            )
//        ).execute()
//    }
//
//    private fun getIpResponse(): retrofit2.Response<UserInfo> {
//        return swapKeyService.getUserDetailedInfo(URL_FOR_USER_INFO).execute()
//    }
//
//    private fun getSignInResponse(userInfo: UserInfo): retrofit2.Response<SignInResponse> {
//        val device = GetDeviceInfo(context).deviceInfo
//        val signInRequest = SignInRequestNew(
//            phone_number = Paper.book().read(Const.PAPER_CLIENT_PHONE),
//            device_type = "A",
//            device_code = context.getDeviceId(),
//            device_name = getDeviceName(),
//            version = "1",
//            ip = context.getIpAddress(),
//            client_id = user_client_id,
//            fcm_token = Paper.book().read(Const.PAPER_FCM_TOKEN) ?: "",
//            password = context.getUserQwerty(),
//            is_pin = 1,
//            sim_iccd = device.sim_iccd.toString(),
//            network_state = device.network_state.toString(),
//            imei_data = device.imei_data.toString(),
//            os_system_version_api = device.os_system_version_api.toString(),
//            os_version = Build.VERSION.SDK_INT.toString(),
//            app_version_code = BuildConfig.VERSION_CODE.toString(),
//            app_version = BuildConfig.VERSION_NAME,
//            userInfo = userInfo,
//            app_key_hash = AppSignatureHelper(context).appKeyHash
//        )
//        return apiInterface.get().signInNew(signInRequest).execute()
//    }
//
//    private fun setKeyForDiffieHellman(swapKeysResponse: SwapKeysResponse?) {
//        val diffieHellman = DiffieHellman.getDiffieHellman()
//        diffieHellman.SetKeyB(swapKeysResponse?.ecnryptData ?: "")
//    }
//
//    private fun isNeedToCallSwapKey(): Boolean {
//        val currentTime = Calendar.getInstance().time
//        return if (this::lastSwapKeyCallTime.isInitialized) {
//            val calculatedTime = calculateTwoDate(currentTime, lastSwapKeyCallTime)
//            calculatedTime > 180
//        } else {
//            lastSwapKeyCallTime = Calendar.getInstance().time
//            true
//        }
//    }
//
//    private fun calculateTwoDate(currentTime: Date, lastUpdateTime: Date): Long {
//        return abs(currentTime.time - lastUpdateTime.time) / 1000
//    }
//
//    companion object {
//        const val TOKEN_EXPIRED = 406
//        const val UNAUTHORIZED = 401
//    }
//
//}