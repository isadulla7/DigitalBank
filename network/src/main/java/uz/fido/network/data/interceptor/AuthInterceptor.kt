package uz.fido.network.data.interceptor

import android.content.Context
import android.os.Build
import android.widget.Toast
import io.paperdb.Paper
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
import uz.fido.utils.utility.activity.insertStringBetween
import uz.fido.utils.utility.context.AppSignatureHelper
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.context.getIpAddress
import uz.fido.utils.utility.language.Utility.getDeviceName
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.utility.user.getFromPaper
import uz.fido.utils.utility.user.saveToPaper
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
                                    modifiedRequest = originalRequest.newBuilder().header("Authorization", context.getClientToken()).build()
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
                modifiedRequest = originalRequest.newBuilder().header("Authorization", context.getClientToken()).build()
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
                phoneNumber = context.getFromPaper(Const.PAPER_CLIENT_PHONE) ?: "".replace("", ""),
            )
        ).execute()
    }

    private fun getIpResponse(): retrofit2.Response<UserInfo> {
        return swapKeyService.getUserDetailedInfo(Keys.getUserInfoUrl() + context.getIpAddress()).execute()
    }

    private fun getSignInResponse(userInfo: UserInfo): retrofit2.Response<SignInResponse> {
        val device = GetDeviceInfo(context).deviceInfo
        val signInRequest = SignInRequestNew(
            phone_number = context.getFromPaper(Const.PAPER_CLIENT_PHONE) ?: "",
            device_type = "A",
            device_code = context.getDeviceIds(),
            device_name = getDeviceName(),
            version = "1",
            ip = context.getIpAddress(),
            client_id = Keys.getClientId(),
            fcm_token = context.getFromPaper(Const.PAPER_FCM_TOKEN),
            password = context.getFromPaper(Const.PASSWORD_ENC),
            is_pin = 1,
            sim_iccd = device.simCcd.toString(),
            network_state = device.networkState.toString(),
            imei_data = device.imeiData.toString(),
            os_system_version_api = "A",
            os_version = Build.VERSION.SDK_INT.toString(),
            app_version_code = context.getFromPaper("VERSION_CODE"),
            app_version = context.getFromPaper("VERSION_NAME"),
            userInfo = userInfo,
            app_key_hash = AppSignatureHelper(context).appKeyHash
        )
        return apiInterface.get().signInNew(context.getClientToken(), signInRequest).execute()
    }

    private fun setKeyForDiffieHellman(swapKeysResponse: SwapKeysResponse?) {
        val diffieHellman = DiffieHellman.getDiffieHellman()
        diffieHellman.SetKeyB(swapKeysResponse?.ecnryptData)
        changeKey(diffieHellman.keyK)
    }

    private fun changeKey(keyK: String) {
        try {
            val key1 = context.getFromPaper(Const.PAPER_CLIENT_PHONE).insertStringBetween("@$#", 3)
            val key2 = context.getFromPaper(Const.PAPER_CLIENT_PHONE).insertStringBetween("&^%", 6)
            val newKey = CryptoUtil.encrypt(
                context.getFromPaper(Const.PASSWORD_ENC), key1
            ) + keyK + CryptoUtil.encrypt(
                context.getFromPaper(Const.STRING_LINE), key2
            )
            context.saveToPaper(Const.KEY_K, newKey)
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
        const val DEVICE_DELETE = 66
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
    Paper.book().write(Const.PAPER_CLIENT_INFO, signInResponse)
    context.run {
        saveToPaper(Const.FIRST_NAME, signInResponse.name)
        saveToPaper(Const.PAPER_CLIENT_ID, signInResponse.user_id)
        saveToPaper(Const.LAST_NAME, signInResponse.surname)
        saveToPaper(Const.PAPER_CLIENT_PHONE, signInResponse.phone_number)
        saveToPaper(Const.PAPER_CLIENT_POINTS, signInResponse.points ?: "0")
        saveToPaper(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
        saveToPaper(Const.PAPER_CLIENT_STATUS_ID, signInResponse.user_status_id)
        saveToPaper(Const.PAPER_CLIENT_STATUS_NAME, signInResponse.user_status_name)
        saveToPaper(Const.PAPER_CLIENT_APPLICATION_COUNT, signInResponse.phone_number)
        saveToPaper(Const.APPLICATION_COUNT, signInResponse.application_count.toString())
        saveToPaper(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
        saveToPaper(Const.PAPER_USER_PHOTO_PATH, profileImageUrl(signInResponse.user_avatar))
    }
}