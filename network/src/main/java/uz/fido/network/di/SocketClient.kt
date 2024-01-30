package uz.fido.network.di

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.fido.network.domain.datasource.services.SocketInterface
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object SocketClient {

    private val loggingInterceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }


    private val baseInterceptor: Interceptor = Interceptor.invoke { chain ->
        val newUrl = chain
            .request()
            .url
            .newBuilder()
            .build()
        val request = chain
            .request()
            .newBuilder()
            .url(newUrl)
            .build()
        return@invoke chain.proceed(request)
    }

    //trust all certificates
    private fun unSafeOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor {
                val request: Request = it.request().newBuilder()
                    .build()
                return@Interceptor it.proceed(request)
            })
            .addInterceptor(loggingInterceptor)
            .addInterceptor(baseInterceptor)
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?, authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?, authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(
                    sslSocketFactory, trustAllCerts.first() as X509TrustManager
                )
                okHttpClient.hostnameVerifier { _, _ -> true }
            }
            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }

    fun retrofitService(): SocketInterface {
        return Retrofit.Builder()
            .baseUrl("https://ss.ubank.uz/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(unSafeOkHttpClient().build())
            .build()
            .create(SocketInterface::class.java)
    }

}