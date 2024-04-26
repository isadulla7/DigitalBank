package uz.fido.network.data.interceptor

import android.content.Context
import android.util.Log
import io.paperdb.Paper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import uz.fido.utils.const.Const
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.user.getUserQwerty
import java.io.IOException
import java.util.*

class EncryptionInterceptor(val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val mediaType: MediaType? = "text/plain; charset=utf-8".toMediaTypeOrNull()
        var request: Request = chain.request()
        var encryptedBody: String? = ""
        val rawBody = request.body

        try {
            val qwertyForEncrypt = if (request.header("Authorization") != null) {
                Log.d("===headers", "has header")
                CryptoUtil.encrypt(
                    context.getUserQwerty(),
                    Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
                        .insertStringBetween("528", 3)
                ) + DiffieHellman.getDiffieHellman().keyK + CryptoUtil.encrypt(
                    Paper.book().read(Const.STRING_LINE),
                    Paper.book().read<String?>(Const.PAPER_CLIENT_PHONE)
                        .insertStringBetween("963", 6)
                )
            } else {
                Log.d("===headers", "there is no header")
                DiffieHellman.getDiffieHellman().keyK + CryptoUtil.encrypt(
                    context.getDeviceIds(),
                    context.getDeviceIds()
                )
            }
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

    private fun String.insertStringBetween(insert: String, index: Int): String {
        return StringBuilder(this).insert(index, insert).toString()
    }

}

