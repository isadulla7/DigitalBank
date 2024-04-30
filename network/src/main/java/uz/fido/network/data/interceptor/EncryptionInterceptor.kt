package uz.fido.network.data.interceptor

import android.content.Context
import android.util.Log
import io.paperdb.Paper
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import uz.fido.utils.const.Const
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.activity.insertStringBetween
import uz.fido.utils.utility.context.getDeviceIds
import java.util.Locale

class EncryptionInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val mediaType: MediaType? = "text/plain; charset=utf-8".toMediaTypeOrNull()
        var request: Request = chain.request()
        var encryptedBody: String? = ""
        val rawBody = request.body

        try {
            val qwertyForEncrypt = Paper.book().read<String>("KEY_K")
            Log.d("====KEY_K enc", Paper.book().read("KEY_K") ?: "no key k")
            /*if (request.header("Authorization") != null) {
            val key1 = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
                .insertStringBetween("$%@", 3)
            val key2 = Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
                .insertStringBetween("*^&", 6)
            CryptoUtil.encrypt(
                Paper.book().read("ENC_PASS"),
                key1
            ) + DiffieHellman.getDiffieHellman().keyK + CryptoUtil.encrypt(
                context.getDeviceIds(),
                context.getDeviceIds()
            ) + CryptoUtil.encrypt(
                Paper.book().read(Const.STRING_LINE),
                key2
            )
        } else {
            DiffieHellman.getDiffieHellman().keyK + CryptoUtil.encrypt(
                context.getDeviceIds(),
                context.getDeviceIds()
            )
        }*/

            val rawBodyString = CryptoUtil.requestBodyToString(rawBody)
            encryptedBody = CryptoUtil.encrypt(rawBodyString, qwertyForEncrypt)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val body = RequestBody.create(mediaType, encryptedBody!!)
        request = getRequest(request, body)

        return chain.proceed(request)
    }

    private fun getRequest(request: Request, requestBody: RequestBody): Request {
        return if (request.method == "GET") {
            request.newBuilder().header(HEADER_CONTENT_TYPE, requestBody.contentType().toString())
                .header(HEADER_CONTENT_LENGTH, requestBody.contentLength().toString())
                .header(HEADER_APP_LANGUAGE, language).build()
        } else {
            request.newBuilder().header(HEADER_CONTENT_TYPE, requestBody.contentType().toString())
                .header(HEADER_CONTENT_LENGTH, requestBody.contentLength().toString())
                .header(HEADER_APP_LANGUAGE, language).method(request.method, requestBody).build()
        }
    }


    private var language =
        Paper.book().read(LanguageConst.LANGUAGE, LanguageConst.RUSSIAN).uppercase(Locale.ROOT)

    private companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_CONTENT_LENGTH = "Content-Length"
        const val HEADER_APP_LANGUAGE = "lang"
    }

}

