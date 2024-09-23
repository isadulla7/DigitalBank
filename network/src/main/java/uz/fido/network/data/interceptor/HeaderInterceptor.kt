package uz.fido.network.data.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import uz.fido.utils.const.Const
import uz.fido.utils.const.LanguageConst
import uz.fido.utils.utility.user.getFromPaper
import java.util.Locale

/**
 * Created by Husniddin Muhammad Amin on 02.05.2023
 * Tashkent, Uzbekistan.
 */

class HeaderInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request =
            chain.request().newBuilder()
                .header(HEADER_APP_VERSION, context.getFromPaper("VERSION_CODE"))
                .header(HEADER_APP_LANGUAGE, language)
                .header(HEADER_DEVICE_TYPE, DEVICE)
                .header(HEADER_DEVICE_CODE, context.getFromPaper(Const.DEVICE_CODE))
                .build()
        return chain.proceed(request)
    }

    private val language = context.getFromPaper(LanguageConst.LANGUAGE, LanguageConst.RUSSIAN)
        .uppercase(Locale.ROOT)
        .replace("RUS", "RU")
        .replace("UZ", "UZL")

    private companion object {
        const val HEADER_APP_VERSION = "version"
        const val HEADER_APP_LANGUAGE = "lang"
        const val HEADER_DEVICE_TYPE = "device"
        const val HEADER_DEVICE_CODE = "devicecode"
        const val DEVICE = "A"
    }

}