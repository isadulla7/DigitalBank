package uz.fido.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
import uz.fido.utils.const.APIServiceConst
import uz.fido.utils.const.MyIdServiceConst
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Arrays
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideBaseUrl(): String = APIServiceConst.BASE_URL

    @Provides
    @Singleton
    fun gsonBuilder(): Gson = GsonBuilder().disableHtmlEscaping().create()

    @Provides
    @Singleton
    fun provideCertificate(@ApplicationContext appContext: Context): InputStream =
        appContext.resources.openRawResource(R.raw.unversal_uz)

    @Provides
    @Singleton
    fun provideKeyStore(caFileInputStream: InputStream): KeyStore = kotlin.run {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val ca = cf.generateCertificate(caFileInputStream)
        keyStore.setCertificateEntry("ca", ca)
        return@run keyStore
    }

    @Provides
    @Singleton
    fun provideKeyManagerFactory(keyStore: KeyStore): KeyManagerFactory = kotlin.run {
        val keyFactory = KeyManagerFactory.getInstance("X509")
        keyFactory.init(keyStore, null)
        return@run keyFactory
    }

    @Provides
    @Singleton
    fun provideSslContext(keyStore: KeyStore, keyManagerFactory: KeyManagerFactory): SSLContext =
        kotlin.run {
            val sslContext = SSLContext.getInstance("TLS")
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)
            sslContext.init(keyManagerFactory.keyManagers, null, SecureRandom())
            return@run sslContext
        }

    @Provides
    @Singleton
    fun provideSslSocketFactory(sslContext: SSLContext): SSLSocketFactory = sslContext.socketFactory

    private fun systemDefaultTrustManager(): X509TrustManager? {
        return try {
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
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

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) httpLoggingInterceptor.level =
            HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @BaseOkhttpClient
    @Provides
    fun provideOkhttpClient(
        @ApplicationContext appContext: Context,
        sslSocketFactory: SSLSocketFactory,
        loggingInterceptor: HttpLoggingInterceptor,
        swapKeyService: SwapKeyApiInterface,
        apiInterface: dagger.Lazy<UserApiInterface>
    ): OkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, systemDefaultTrustManager() as X509TrustManager)
        .addInterceptor(HeaderInterceptor())
       // .addInterceptor(loggingInterceptor)
        .addInterceptor(
            AuthInterceptor(
                swapKeyService = swapKeyService, context = appContext, apiInterface
            )
        )
   //     .addInterceptor(ChuckerInterceptor.Builder(appContext).build())
        .addInterceptor(EncryptionInterceptor())
        .addInterceptor(DecryptionInterceptor())
        .readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS).build()

    @SimpleClientRetrofit
    @Provides
    fun provideSimpleOkhttpClient(
        @ApplicationContext appContext: Context,
    ): OkHttpClient = OkHttpClient.Builder()
     //   .sslSocketFactory(sslSocketFactory, systemDefaultTrustManager() as X509TrustManager)
     //   .addInterceptor(ChuckerInterceptor.Builder(appContext).build())
        .readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS).build()

    @BaseRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String, @BaseOkhttpClient okHttpClient: OkHttpClient, gsonBuilder: Gson
    ): Retrofit = Retrofit.Builder().client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder)).baseUrl(baseUrl).build()

    /*
    *   MY ID RETROFIT CLIENT
    */

    @MyIdOkhttpClient
    @Provides
    fun provideMyIdRetrofitClient(
        @ApplicationContext appContext: Context, loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
        //    .addInterceptor(loggingInterceptor)
        //    .addInterceptor(ChuckerInterceptor.Builder(appContext).build())
            .readTimeout(180, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS).build()

    @MyIdRetrofit
    @Provides
    @Singleton
    fun provideMyIdRetrofit(@MyIdOkhttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
            .baseUrl(MyIdServiceConst.MY_ID_URL).build()

    /*
    *   SOCKET RETROFIT CLIENT
    */

    @SocketRetrofit
    @Provides
    @Singleton
    fun provideSocketRetrofit(@BaseOkhttpClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
            .baseUrl(APIServiceConst.SOCKET_URL).build()

    @SimpleClientRetrofit
    @Provides
    fun provideSimpleRetrofit(@SimpleClientRetrofit okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
            .baseUrl(APIServiceConst.BASE_URL).build()

    /*
    *   SWAP KEY RETROFIT CLIENT
    */

    @SwapKeyRetrofit
    @Provides
    fun swapKeyRetrofitClient(
        @ApplicationContext appContext: Context, sslSocketFactory: SSLSocketFactory,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, systemDefaultTrustManager() as X509TrustManager)
        .addInterceptor(Interceptor {
            val request: Request = it.request().newBuilder().build()
            return@Interceptor it.proceed(request)
        })
      //  .addInterceptor(loggingInterceptor)
      //  .addInterceptor(ChuckerInterceptor.Builder(appContext).build())
        .readTimeout(180, TimeUnit.SECONDS)
        .connectTimeout(180, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).build()

    @SwapKeyRetrofit
    @Provides
    @Singleton
    fun swapKeyRetrofit(
        baseUrl: String, @SwapKeyRetrofit okHttpClient: OkHttpClient, gsonBuilder: Gson
    ): Retrofit = Retrofit.Builder().client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder)).baseUrl(baseUrl).build()

    private fun unSafeOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
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

}