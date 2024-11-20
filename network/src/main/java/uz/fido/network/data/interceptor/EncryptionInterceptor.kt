package uz.fido.network.data.interceptor

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import uz.fido.utils.const.Const
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.utility.user.getFromPaper
import java.util.Locale

class EncryptionInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val mediaType: MediaType? = MEDIA_TYPE.toMediaTypeOrNull()
        var request: Request = chain.request()
        val rawBody = request.body
        var encryptedBody = ""
        if (rawBody != null) {
            try {
                val rawBodyString = CryptoUtil.requestBodyToString(rawBody)
                encryptedBody = CryptoUtil.encrypt(rawBodyString, context.getFromPaper(Const.KEY_K))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val body = encryptedBody.toRequestBody(mediaType)
            request = getRequest(request, body)
        } else {
            request = getRequest(request)
        }
        return chain.proceed(request)
    }

    private fun getRequest(request: Request, requestBody: RequestBody? = null): Request {
        var language=when(context.getFromPaper(LanguageConst.LANGUAGE, LanguageConst.RUSSIAN)){
            "uz"->"UZL"
            "en"->"EN"
            else->"RU"

        }
        return if (request.method == "GET") {
            request.newBuilder()
                .header(HEADER_APP_LANGUAGE, language).build()
        } else {
            request.newBuilder().header(HEADER_CONTENT_TYPE, requestBody?.contentType().toString())
                .header(HEADER_CONTENT_LENGTH, requestBody?.contentLength().toString())
                .header(HEADER_APP_LANGUAGE, language).method(request.method, requestBody).build()
        }
    }


    private var language = context.getFromPaper(LanguageConst.LANGUAGE, LanguageConst.RUSSIAN).uppercase(Locale.ROOT)

    private companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_CONTENT_LENGTH = "Content-Length"
        const val HEADER_APP_LANGUAGE = "lang"
        const val MEDIA_TYPE = "text/plain; charset=utf-8"
    }

}

