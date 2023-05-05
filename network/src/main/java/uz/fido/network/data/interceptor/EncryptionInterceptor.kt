package uz.fido.network.data.interceptor

import io.paperdb.Paper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import java.io.IOException
import java.util.*

class EncryptionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val mediaType: MediaType? = "text/plain; charset=utf-8".toMediaTypeOrNull()
        var request: Request = chain.request()
        var encryptedBody: String? = ""
        val rawBody = request.body

        try {
            val qwertyForEncrypt = DiffieHellman.getDiffieHellman().keyK
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
            request.newBuilder()
                .header(HEADER_CONTENT_TYPE, requestBody.contentType().toString())
                .header(HEADER_CONTENT_LENGTH, requestBody.contentLength().toString())
                .header(HEADER_APP_LANGUAGE, language)
                .build()
        } else {
            request.newBuilder()
                .header(HEADER_CONTENT_TYPE, requestBody.contentType().toString())
                .header(HEADER_CONTENT_LENGTH, requestBody.contentLength().toString())
                .header(HEADER_APP_LANGUAGE, language)
                .method(request.method, requestBody).build()
        }
    }

    private var language = Paper.book().read(LanguageConst.LANGUAGE, LanguageConst.RUSSIAN)
        .uppercase(Locale.ROOT)

    private companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_CONTENT_LENGTH = "Content-Length"
        const val HEADER_APP_LANGUAGE = "lang"
    }

}

