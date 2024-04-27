package uz.fido.network.data.interceptor

import android.content.Context
import android.text.TextUtils
import io.paperdb.Paper
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import uz.fido.utils.const.Const
import uz.fido.utils.log.Log
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.user.getUserQwerty
import java.io.IOException

/**
 * Created by Husniddin Muhammad Amin on 02.05.2023
 * Tashkent, Uzbekistan.
 */

/**
 *
 * Retrofit Interceptor to intercept and decrypt response from the server
 */


class DecryptionInterceptor(val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(chain.request())
        val newResponse = response.newBuilder()

        var contentType = response.header("Content-Type")

        if (TextUtils.isEmpty(contentType)) contentType = "application/json"

        if (response.body != null) {
            val responseString = response.peekBody(Long.MAX_VALUE).string()
            var decryptedString: String? = null
            try {
                val qwertyForEncrypt = if (request.header("Authorization") != null) {
                    val key1 = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
                        .insertStringBetween("528", 3)
                    val key2 = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
                        .insertStringBetween("963", 6)
                    CryptoUtil.encrypt(
                        Paper.book().read("ENC_PASS"),
                        key1
                    ) + DiffieHellman.getDiffieHellman().keyK + CryptoUtil.encrypt(
                        Paper.book().read(Const.STRING_LINE),
                        key2
                    )
                } else {
                    DiffieHellman.getDiffieHellman().keyK + CryptoUtil.encrypt(
                        context.getDeviceIds(),
                        context.getDeviceIds()
                    )
                }
                decryptedString = CryptoUtil.decrypt(responseString, qwertyForEncrypt)
            } catch (e: Exception) {
                Log.d("===headers dec", e.toString())
                e.printStackTrace()
            }
            if (decryptedString != null) {
                newResponse.body(
                    ResponseBody.create(
                        contentType.toString().toMediaTypeOrNull(),
                        decryptedString
                    )
                )
            }
        }

        return newResponse.build()
    }

    private fun String.insertStringBetween(insert: String, index: Int): String {
        return StringBuilder(this).insert(index, insert).toString()
    }

}