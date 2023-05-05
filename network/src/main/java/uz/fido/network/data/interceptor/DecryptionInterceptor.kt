package uz.fido.network.data.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import java.io.IOException

/**
 * Created by Husniddin Muhammad Amin on 02.05.2023
 * Tashkent, Uzbekistan.
 */

/**
 *
 * Retrofit Interceptor to intercept and decrypt response from the server
 */


class DecryptionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())
        val newResponse = response.newBuilder()

        var contentType = response.header("Content-Type")

        if (TextUtils.isEmpty(contentType)) contentType = "application/json"

        if (response.body != null) {
            val responseString = response.peekBody(Long.MAX_VALUE).string()
            var decryptedString: String? = null
            try {
                decryptedString = CryptoUtil.decrypt(responseString, DiffieHellman.getDiffieHellman().keyK)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (decryptedString != null) {
                newResponse.body(ResponseBody.create(contentType.toString().toMediaTypeOrNull(), decryptedString))
            }
        }

        return newResponse.build()

    }

}