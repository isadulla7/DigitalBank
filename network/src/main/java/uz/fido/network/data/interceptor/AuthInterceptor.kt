package uz.fido.network.data.interceptor

import android.content.Context
import android.os.Build
import android.widget.Toast
import io.paperdb.Paper
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import uz.fido.network.data.utility.CurrentActivityHolder
import uz.fido.network.domain.datasource.services.SwapKeyApiInterface
import uz.fido.network.domain.datasource.services.UserApiInterface
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.utils.const.APIServiceConst
import uz.fido.utils.const.APIServiceConst.USER_CLIENT_ID
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
import uz.fido.utils.utility.user.getUserQwerty
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs
import kotlin.system.exitProcess

/**
 * Created by Husniddin Muhammad Amin on 02.05.2023
 * Tashkent, Uzbekistan.
 */

class AuthInterceptor @Inject constructor(
    private val swapKeyService: SwapKeyApiInterface,
    private val context: Context,
    private val apiInterface: dagger.Lazy<UserApiInterface>
) : Interceptor {

    private lateinit var lastSwapKeyCallTime: Date

    override fun intercept(chain: Interceptor.Chain): Response {
        var modifiedRequest: Request? = null
        val originalRequest = chain.request()
        val originalResponse = chain.proceed(originalRequest)
        val activity = CurrentActivityHolder.currentActivity

        if (originalResponse.code != TOKEN_EXPIRED && originalResponse.code != UNAUTHORIZED) {
            return originalResponse
        }

        if (originalResponse.code == UNAUTHORIZED) {
            try {
                val jsonObject = JSONObject(originalResponse.body?.string() ?: "")
                val code = jsonObject.getInt("code")
                if (code == DEVICE_DELETE) {
                    activity?.finishAffinity()
                    exitProcess(0)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "error code", Toast.LENGTH_SHORT).show()
            }
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
                                    saveSignInPinResponse(it)
                                    modifiedRequest = originalRequest.newBuilder()
                                        .header("Authorization", getClientToken()).build()
                                    return chain.proceed(modifiedRequest!!)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, ipResponse.message(), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, swapKeysResponse.message(), Toast.LENGTH_SHORT).show()
                }
            } else {
                modifiedRequest =
                    originalRequest.newBuilder().header("Authorization", getClientToken()).build()
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
                phoneNumber = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE) ?: "".replace("", ""),
            )
        ).execute()
    }

    private fun getIpResponse(): retrofit2.Response<UserInfo> {
        return swapKeyService.getUserDetailedInfo(APIServiceConst.USER_INFO_URL + context.getIpAddress())
            .execute()
    }

    private fun getSignInResponse(userInfo: UserInfo): retrofit2.Response<SignInResponse> {
        val device = GetDeviceInfo(context).deviceInfo
        val signInRequest = SignInRequestNew(
            phone_number = Paper.book().read(Const.PAPER_CLIENT_PHONE),
            device_type = "A",
            device_code = context.getDeviceIds(),
            device_name = getDeviceName(),
            version = "1",
            ip = context.getIpAddress(),
            client_id = USER_CLIENT_ID,
            fcm_token = Paper.book().read(Const.PAPER_FCM_TOKEN) ?: "",
            password = context.getUserQwerty(),
            is_pin = 1,
            sim_iccd = device.simCcd.toString(),
            network_state = device.networkState.toString(),
            imei_data = device.imeiData.toString(),
            os_system_version_api = "A",
            os_version = Build.VERSION.SDK_INT.toString(),
            app_version_code = Paper.book().read<String>("VERSION_CODE").toString(),
            app_version = Paper.book().read<String?>("VERSION_NAME").toString(),
            userInfo = userInfo,
            app_key_hash = AppSignatureHelper(context).appKeyHash
        )
        return apiInterface.get().signInNew(getClientToken(), signInRequest).execute()
    }

    private fun setKeyForDiffieHellman(swapKeysResponse: SwapKeysResponse?) {
        DiffieHellman.clearDiffieHellman()
        DiffieHellman.getDiffieHellman().SetKeyB(swapKeysResponse?.ecnryptData)
        changeKey(DiffieHellman.getDiffieHellman().keyK)
    }

    private fun changeKey(keyK: String) {
        val key1 = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
            .insertStringBetween("@$#", 3)
        val key2 = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
            .insertStringBetween("&^%", 6)
        val newKey = CryptoUtil.encrypt(
            Paper.book().read("ENC_PASS"),
            key1
        ) + keyK + CryptoUtil.encrypt(
            Paper.book().read(Const.STRING_LINE),
            key2
        )
        Paper.book().write("KEY_K", newKey)
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

fun saveSignInPinResponse(signInResponse: SignInResponse) {
    Paper.book().write(Const.PAPER_CLIENT_INFO, signInResponse)
    Paper.book().write(Const.PAPER_CLIENT_NAME, signInResponse.name)
    Paper.book().write(Const.PAPER_CLIENT_ID, signInResponse.user_id)
    Paper.book().write(Const.PAPER_CLIENT_SURNAME, signInResponse.surname)
    Paper.book().write(Const.PAPER_CLIENT_PHONE, signInResponse.phone_number)
    Paper.book().write(Const.PAPER_CLIENT_POINTS, signInResponse.points ?: "0")
    Paper.book().write(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
    Paper.book().write(Const.PAPER_CLIENT_STATUS_ID, signInResponse.user_status_id)
    Paper.book().write(Const.PAPER_CLIENT_STATUS_NAME, signInResponse.user_status_name)
    Paper.book().write(Const.PAPER_CLIENT_APPLICATION_COUNT, signInResponse.phone_number)
    Paper.book().write(Const.APPLICATION_COUNT, signInResponse.application_count.toString())
    Paper.book().write(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
    Paper.book().write(Const.PAPER_USER_PHOTO_PATH, profileImageUrl(signInResponse.user_avatar))
}