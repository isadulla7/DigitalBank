package uz.fido.network.data.interceptor

import android.text.TextUtils
import io.paperdb.Paper
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import uz.fido.utils.security.CryptoUtil
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
        var contentType = response.header(CONTENT_TYPE)
        if (TextUtils.isEmpty(contentType)) contentType = APP_JSON

        if (response.body != null) {
            val responseString = response.peekBody(Long.MAX_VALUE).string()
            var decryptedString: String? = null
            try {
                decryptedString = CryptoUtil.decrypt(responseString, Paper.book().read("KEY_K"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (decryptedString != null) {
                newResponse.body(decryptedString.toResponseBody(contentType.toString().toMediaTypeOrNull()))
            }
        }
        return newResponse.build()
    }

    companion object {
        const val CONTENT_TYPE = "Content-Type"
        const val APP_JSON = "application/json"
    }
}