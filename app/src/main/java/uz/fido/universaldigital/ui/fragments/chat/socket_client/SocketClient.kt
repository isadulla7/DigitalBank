package uz.fido.universaldigital.ui.fragments.chat.socket_client

import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.fido.network.BuildConfig
import uz.fido.network.R
import uz.fido.network.di.Keys
import uz.fido.network.domain.datasource.services.SocketInterface
import uz.fido.universaldigital.app.UniversalApplication
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.SecureRandom
import java.util.Arrays
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object SocketClient {

    private val loggingInterceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            if (BuildConfig.DEBUG) httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
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

    private fun systemDefaultTrustManager(): X509TrustManager? {
        return try {
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            }
            trustManagers[0] as X509TrustManager
        } catch (e: GeneralSecurityException) {
            throw AssertionError()
        }
    }

    private var caFileInputStream = UniversalApplication.getContext().resources.openRawResource(R.raw.mycertificate)

    private val sslContext: SSLContext = kotlin.run {
        val keyStore = KeyStore.getInstance("PKCS12")
        val password = Keys.getCertFilePassword().toCharArray()
        try {
            keyStore.load(caFileInputStream, password)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val keyManagerFactory = KeyManagerFactory.getInstance("X509")
        keyManagerFactory.init(keyStore, password)
        val sslContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            SSLContext.getInstance("TLSv1.3")
        } else {
            SSLContext.getInstance("TLSv1.2")
        }
        sslContext.init(keyManagerFactory.keyManagers, null, SecureRandom())
        return@run sslContext
    }

    private val sslSocketFactory = sslContext.socketFactory

    private val client: OkHttpClient =
        OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, systemDefaultTrustManager() as X509TrustManager)
            .addInterceptor(Interceptor {
                val request: Request = it.request().newBuilder().build()
                return@Interceptor it.proceed(request)
            })
            .addInterceptor(baseInterceptor)
            .addInterceptor(loggingInterceptor)
            .readTimeout(180, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS)
            .build()

    fun retrofitService(): SocketInterface {
        return Retrofit.Builder()
            .baseUrl(Keys.getSocketUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SocketInterface::class.java)
    }

}