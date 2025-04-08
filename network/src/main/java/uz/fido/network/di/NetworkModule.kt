package uz.fido.network.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.fido.network.BuildConfig
import uz.fido.network.R
import uz.fido.network.data.interceptor.AuthInterceptor
import uz.fido.network.data.interceptor.DecryptionInterceptor
import uz.fido.network.data.interceptor.EncryptionInterceptor
import uz.fido.network.data.interceptor.HeaderInterceptor
import uz.fido.network.domain.datasource.services.SwapKeyApiInterface
import uz.fido.network.domain.datasource.services.UserApiInterface
import uz.fido.utils.const.MyIdServiceConst
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideBaseUrl(): String = Keys.getBaseUrl()

    @Provides
    @Singleton
    fun gsonBuilder(): Gson = GsonBuilder().disableHtmlEscaping().create()

    @Provides
    @Singleton
    fun provideCertificate(@ApplicationContext appContext: Context): InputStream = appContext.resources.openRawResource(R.raw.mycertificate)

    @Provides
    @Singleton
    fun provideKeyStore(caFileInputStream: InputStream): KeyStore = kotlin.run {
        val keyStore = KeyStore.getInstance("PKCS12")
        val password = Keys.getCertFilePassword().toCharArray()
        caFileInputStream.use {
            keyStore.load(it, password)
        }
        return keyStore
    }

    @Provides
    @Singleton
    fun provideKeyManagerFactory(keyStore: KeyStore): KeyManagerFactory = kotlin.run {
        val keyFactory = KeyManagerFactory.getInstance("X509")
        keyFactory.init(keyStore, Keys.getCertFilePassword().toCharArray())
        return@run keyFactory
    }

    @Provides
    @Singleton
    fun provideSslContext(keyManagerFactory: KeyManagerFactory): SSLContext =
        kotlin.run {
            val sslContext = SSLContext.getInstance("TLSv1.2")
            val tmf = systemDefaultTrustManager()
            sslContext.init(keyManagerFactory.keyManagers, arrayOf(tmf), SecureRandom())
            return@run sslContext
        }

    @Provides
    @Singleton
    fun provideSslSocketFactory(sslContext: SSLContext): SSLSocketFactory = sslContext.socketFactory

    private fun systemDefaultTrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        require(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) {
            "Unexpected TrustManager configuration"
        }
        return trustManagers[0] as X509TrustManager
    }

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    private val certificatePinner = CertificatePinner.Builder()
        .add(Keys.getDomainName(), Keys.getCertPin1())
        .add(Keys.getDomainName(), Keys.getCertPin2())
        .add(Keys.getDomainName(), Keys.getCertPin3())
        .build()

    @BaseOkhttpClient
    @Provides
    fun provideOkhttpClient(
        @ApplicationContext appContext: Context,
        sslSocketFactory: SSLSocketFactory,
        loggingInterceptor: HttpLoggingInterceptor,
        swapKeyService: SwapKeyApiInterface,
        apiInterface: dagger.Lazy<UserApiInterface>,
    ): OkHttpClient = OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, systemDefaultTrustManager())
        .certificatePinner(certificatePinner)
        .addInterceptor(HeaderInterceptor(context = appContext))
        .addInterceptor(loggingInterceptor)
        .addInterceptor(
            AuthInterceptor(swapKeyService = swapKeyService, context = appContext, apiInterface)
        ).addInterceptor(EncryptionInterceptor(appContext))
        .addInterceptor(DecryptionInterceptor(appContext))
        .readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .build()

    @SimpleClientRetrofit
    @Provides
    fun provideSimpleOkhttpClient(): OkHttpClient = OkHttpClient.Builder().readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).build()

    @BaseRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String, @BaseOkhttpClient okHttpClient: OkHttpClient, gsonBuilder: Gson
    ): Retrofit = Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gsonBuilder)).baseUrl(baseUrl).build()

    /*
    *   MY ID RETROFIT CLIENT
    */

    @MyIdOkhttpClient
    @Provides
    fun provideMyIdRetrofitClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS).build()

    @MyIdRetrofit
    @Provides
    @Singleton
    fun provideMyIdRetrofit(@MyIdOkhttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).baseUrl(MyIdServiceConst.MY_ID_URL).build()

    /*
    *   SOCKET RETROFIT CLIENT
    */

    @SocketRetrofit
    @Provides
    @Singleton
    fun provideSocketRetrofit(@BaseOkhttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).baseUrl(Keys.getSocketUrl()).build()

    @SimpleClientRetrofit
    @Provides
    fun provideSimpleRetrofit(@SimpleClientRetrofit okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).baseUrl(Keys.getBaseUrl()).build()

    /*
    *   SWAP KEY RETROFIT CLIENT
    */

    @SwapKeyRetrofit
    @Provides
    fun swapKeyRetrofitClient(sslSocketFactory: SSLSocketFactory, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, systemDefaultTrustManager())
            .certificatePinner(certificatePinner)
            .addInterceptor(Interceptor {
                val request: Request = it.request().newBuilder().build()
                return@Interceptor it.proceed(request)
            }).addInterceptor(loggingInterceptor).readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).build()

    @SwapKeyRetrofit
    @Provides
    @Singleton
    fun swapKeyRetrofit(
        baseUrl: String, @SwapKeyRetrofit okHttpClient: OkHttpClient, gsonBuilder: Gson
    ): Retrofit = Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gsonBuilder)).baseUrl(baseUrl).build()

}