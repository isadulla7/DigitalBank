package uz.fido.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uz.fido.network.domain.datasource.services.CardApiInterface
import uz.fido.network.domain.datasource.services.ChatApiInterface
import uz.fido.network.domain.datasource.services.CreditApiInterface
import uz.fido.network.domain.datasource.services.DepositApiInterface
import uz.fido.network.domain.datasource.services.MonitoringApiInterface
import uz.fido.network.domain.datasource.services.MyIdApiInterface
import uz.fido.network.domain.datasource.services.P2PApiInterface
import uz.fido.network.domain.datasource.services.PaymentApiInterface
import uz.fido.network.domain.datasource.services.ServiceApiInterface
import uz.fido.network.domain.datasource.services.SocketInterface
import uz.fido.network.domain.datasource.services.SwapKeyApiInterface
import uz.fido.network.domain.datasource.services.TemplateApiInterface
import uz.fido.network.domain.datasource.services.UserApiInterface
import uz.fido.network.domain.datasource.services.UtilsApiInterface
import uz.fido.network.domain.datasource.services.WalletApiInterface
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideCardApiService(@BaseRetrofit retrofit: Retrofit): CardApiInterface = retrofit.create(
        CardApiInterface::class.java
    )

    @Provides
    @Singleton
    fun provideChatApiService(@BaseRetrofit retrofit: Retrofit): ChatApiInterface = retrofit.create(
        ChatApiInterface::class.java
    )

    @Provides
    @Singleton
    fun provideCreditApiService(@BaseRetrofit retrofit: Retrofit): CreditApiInterface =
        retrofit.create(
            CreditApiInterface::class.java
        )

    @Provides
    @Singleton
    fun provideDepositApiService(@BaseRetrofit retrofit: Retrofit): DepositApiInterface =
        retrofit.create(
            DepositApiInterface::class.java
        )

    @Provides
    @Singleton
    fun provideMonitoringApiService(@BaseRetrofit retrofit: Retrofit): MonitoringApiInterface =
        retrofit.create(
            MonitoringApiInterface::class.java
        )

    @Provides
    @Singleton
    fun provideMyIdService(@MyIdRetrofit retrofit: Retrofit): MyIdApiInterface =
        retrofit.create(MyIdApiInterface::class.java)

    @Provides
    @Singleton
    fun provideP2pApiService(@BaseRetrofit retrofit: Retrofit): P2PApiInterface = retrofit.create(
        P2PApiInterface::class.java
    )

    @Provides
    @Singleton
    fun providePaymentApiService(@BaseRetrofit retrofit: Retrofit): PaymentApiInterface =
        retrofit.create(
            PaymentApiInterface::class.java
        )

    @Provides
    @Singleton
    fun provideServiceApiService(@BaseRetrofit retrofit: Retrofit): ServiceApiInterface =
        retrofit.create(
            ServiceApiInterface::class.java
        )

    @Provides
    @Singleton
    fun provideSocketApiService(@SocketRetrofit retrofit: Retrofit): SocketInterface =
        retrofit.create(SocketInterface::class.java)

    @Provides
    @Singleton
    fun swapKeyService(@SwapKeyRetrofit retrofit: Retrofit): SwapKeyApiInterface =
        retrofit.create(SwapKeyApiInterface::class.java)

    @Provides
    @Singleton
    fun provideTemplateApiService(@BaseRetrofit retrofit: Retrofit): TemplateApiInterface =
        retrofit.create(
            TemplateApiInterface::class.java
        )

    @Provides
    fun provideUserApiService(@BaseRetrofit retrofit: Retrofit): UserApiInterface = retrofit.create(
        UserApiInterface::class.java
    )

    @Provides
    fun provideUtilsApiService(@BaseRetrofit retrofit: Retrofit): UtilsApiInterface =
        retrofit.create(
            UtilsApiInterface::class.java
        )

    @Provides
    fun provideWalletApiService(@BaseRetrofit retrofit: Retrofit): WalletApiInterface =
        retrofit.create(
            WalletApiInterface::class.java
        )

}