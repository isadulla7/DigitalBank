package uz.fido.network.data.interceptor

import android.content.Context
import android.os.Build
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import uz.fido.network.di.Keys
import uz.fido.network.domain.datasource.services.SwapKeyApiInterface
import uz.fido.network.domain.datasource.services.UserApiInterface
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.utils.const.APIServiceConst.profileImageUrl
import uz.fido.utils.const.Const
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.getClientEncodedToken
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.activity.insertStringBetween
import uz.fido.utils.utility.context.AppSignatureHelper
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.utility.user.getClientToken
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs

/**
 * Created by Husniddin Muhammad Amin on 02.05.2023
 * Tashkent, Uzbekistan.
 */

class AuthInterceptor @Inject constructor(
    private val swapKeyService: SwapKeyApiInterface, private val context: Context, private val apiInterface: dagger.Lazy<UserApiInterface>
) : Interceptor {

    private lateinit var lastSwapKeyCallTime: Date

    override fun intercept(chain: Interceptor.Chain): Response {
        var modifiedRequest: Request? = null
        val originalRequest = chain.request()
        val originalResponse = chain.proceed(originalRequest)

        if (originalResponse.code != TOKEN_EXPIRED && originalResponse.code != UNAUTHORIZED) {
            return originalResponse
        }

        synchronized(this) {
            if (isNeedToCallSwapKey()) {
                lastSwapKeyCallTime = Calendar.getInstance().time
                val swapKeysResponse = getSwapKeyResponse()
                if (swapKeysResponse.code() == 200) {
                    setKeyForDiffieHellman(swapKeysResponse.body())
                    val ipResponse = getIpResponse()
                    if (ipResponse.code() == 200) {
                        ipResponse.body()?.let { userInfo ->
                            val signInResponse = getSignInResponse(userInfo)
                            if (signInResponse.code() == 200) {
                                signInResponse.body()?.let {
                                    saveSignInPinResponse(context, it)
                                    modifiedRequest = originalRequest.newBuilder().header("Authorization", getClientToken()).build()
                                    return chain.proceed(modifiedRequest!!)
                                }
                            }
                        }
                    } else {
                        tryMakeToast(ipResponse.message(), context)
                    }
                } else {
                    tryMakeToast(swapKeysResponse.message(), context)
                }
            } else {
                modifiedRequest = originalRequest.newBuilder().header("Authorization", getClientToken()).build()
                return chain.proceed(modifiedRequest!!)
            }
        }
        return if (modifiedRequest != null) chain.proceed(modifiedRequest!!) else originalResponse
    }

    private fun getSwapKeyResponse(): retrofit2.Response<SwapKeysResponse> {
        return swapKeyService.swapKey(
            SwapKeysRequest(
                device_code = context.getDeviceIds(),
                public_key1 = DiffieHellman.getDiffieHellman()._g.toBigInteger(),
                public_key2 = DiffieHellman.getDiffieHellman()._p.toBigInteger(),
                encryptData = DiffieHellman.getDiffieHellman().keyA,
                phoneNumber = context.getFromSecureStore(Const.PAPER_CLIENT_PHONE),
            )
        ).execute()
    }

    private fun getIpResponse(): retrofit2.Response<UserInfo> {
        return swapKeyService.getUserDetailedInfo(Keys.getUserInfoUrl() + context.getIpAddress()).execute()
    }

    private fun getSignInResponse(userInfo: UserInfo): retrofit2.Response<SignInResponse> {
        val device = GetDeviceInfo(context).deviceInfo
        val signInRequest = SignInRequestNew(
            phone_number = context.getFromSecureStore(Const.PAPER_CLIENT_PHONE),
            device_type = "A",
            device_code = context.getDeviceIds(),
            device_name = getDeviceName(),
            version = "1",
            ip = context.getIpAddress(),
            client_id = Keys.getClientId(),
            fcm_token = context.getFromSecureStore(Const.PAPER_FCM_TOKEN),
            password = context.getFromSecureStore(Const.PASSWORD_ENC),
            is_pin = 1,
            sim_iccd = device.simCcd.toString(),
            network_state = device.networkState.toString(),
            imei_data = device.imeiData.toString(),
            os_system_version_api = "A",
            os_version = Build.VERSION.SDK_INT.toString(),
            app_version_code = context.getFromSecureStore(Const.VERSION_CODE),
            app_version = context.getFromSecureStore("VERSION_NAME"),
            userInfo = userInfo,
            app_key_hash = AppSignatureHelper(context).appKeyHash
        )
        return apiInterface.get().signInNew(getClientToken(), signInRequest).execute()
    }

    private fun setKeyForDiffieHellman(swapKeysResponse: SwapKeysResponse?) {
        val diffieHellman = DiffieHellman.getDiffieHellman()
        diffieHellman.SetKeyB(swapKeysResponse?.ecnryptData)
        changeKey(diffieHellman.keyK)
    }

    private fun changeKey(keyK: String) {
        try {
            val key1 = context.getFromSecureStore(Const.PAPER_CLIENT_PHONE).insertStringBetween("@$#", 3)
            val key2 = context.getFromSecureStore(Const.PAPER_CLIENT_PHONE).insertStringBetween("&^%", 6)
            val newKey = CryptoUtil.encrypt(
                context.getFromSecureStore(Const.PASSWORD_ENC), key1
            ) + keyK + CryptoUtil.encrypt(
                context.getFromSecureStore(Const.STRING_LINE), key2
            )
            saveToSecureStore(Const.KEY_K, newKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isNeedToCallSwapKey(): Boolean {
        val currentTime = Calendar.getInstance().time
        return if (this::lastSwapKeyCallTime.isInitialized) {
            val calculatedTime = calculateTwoDate(currentTime, lastSwapKeyCallTime)
            calculatedTime > 180
        } else {
            lastSwapKeyCallTime = Calendar.getInstance().time
            true
        }
    }

    private fun calculateTwoDate(currentTime: Date, lastUpdateTime: Date): Long {
        return abs(currentTime.time - lastUpdateTime.time) / 1000
    }

    companion object {
        const val TOKEN_EXPIRED = 406
        const val UNAUTHORIZED = 401
    }

}

fun tryMakeToast(message: String, context: Context) {
    try {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun saveSignInPinResponse(context: Context, signInResponse: SignInResponse) {
    context.run {
        saveToSecureStore(Const.FIRST_NAME, signInResponse.name)
        saveToSecureStore(Const.PAPER_CLIENT_ID, signInResponse.user_id)
        saveToSecureStore(Const.LAST_NAME, signInResponse.surname)
        saveToSecureStore(Const.PAPER_CLIENT_PHONE, signInResponse.phone_number)
        saveToSecureStore(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
        saveToSecureStore(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
        saveToSecureStore(Const.PAPER_USER_PHOTO_PATH, profileImageUrl(signInResponse.user_avatar))
    }
}