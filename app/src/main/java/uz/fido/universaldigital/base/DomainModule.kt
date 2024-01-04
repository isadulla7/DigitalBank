package uz.fido.universaldigital.base

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.fido.network.data.repository.CardRepositoryImpl
import uz.fido.network.data.repository.ChatRepositoryImpl
import uz.fido.network.data.repository.CreditRepositoryImpl
import uz.fido.network.data.repository.DepositRepositoryImpl
import uz.fido.network.data.repository.MonitoringRepositoryImpl
import uz.fido.network.data.repository.MyIdRepositoryImpl
import uz.fido.network.data.repository.P2PRepositoryImpl
import uz.fido.network.data.repository.PaymentRepositoryImpl
import uz.fido.network.data.repository.ServiceRepositoryImpl
import uz.fido.network.data.repository.SocketRepositoryImpl
import uz.fido.network.data.repository.SwapKeyRepositoryImpl
import uz.fido.network.data.repository.TemplateRepositoryImpl
import uz.fido.network.data.repository.UserRepositoryImpl
import uz.fido.network.data.repository.UtilsRepositoryImpl
import uz.fido.network.data.repository.WalletRepositoryImpl
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.datasource.interfaces.IChatRepository
import uz.fido.network.domain.datasource.interfaces.ICreditRepository
import uz.fido.network.domain.datasource.interfaces.IDepositRepository
import uz.fido.network.domain.datasource.interfaces.IMonitoringRepository
import uz.fido.network.domain.datasource.interfaces.IMyIdRepository
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.IServiceRepository
import uz.fido.network.domain.datasource.interfaces.ISocketRepository
import uz.fido.network.domain.datasource.interfaces.ISwapKeyRepository
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.datasource.interfaces.IWalletRepository
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

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideCardRepository(apiInterface: CardApiInterface): ICardRepository =
        CardRepositoryImpl(apiInterface)

    @Provides
    fun provideChatRepository(apiInterface: ChatApiInterface): IChatRepository =
        ChatRepositoryImpl(apiInterface)

    @Provides
    fun provideCreditRepository(apiInterface: CreditApiInterface): ICreditRepository =
        CreditRepositoryImpl(apiInterface)

    @Provides
    fun provideDepositRepository(apiInterface: DepositApiInterface): IDepositRepository =
        DepositRepositoryImpl(apiInterface)

    @Provides
    fun provideMonitoringRepository(apiInterface: MonitoringApiInterface): IMonitoringRepository =
        MonitoringRepositoryImpl(apiInterface)

    @Provides
    fun provideMyIdRepository(apiInterface: MyIdApiInterface): IMyIdRepository =
        MyIdRepositoryImpl(apiInterface)

    @Provides
    fun provideP2PRepository(apiInterface: P2PApiInterface): IP2PRepository =
        P2PRepositoryImpl(apiInterface)

    @Provides
    fun providePaymentRepository(apiInterface: PaymentApiInterface): IPaymentRepository =
        PaymentRepositoryImpl(apiInterface)

    @Provides
    fun provideServiceRepository(apiInterface: ServiceApiInterface): IServiceRepository =
        ServiceRepositoryImpl(apiInterface)

    @Provides
    fun provideSocketRepository(apiInterface: SocketInterface): ISocketRepository =
        SocketRepositoryImpl(apiInterface)

    @Provides
    fun provideSwapKeyRepository(apiInterface: SwapKeyApiInterface): ISwapKeyRepository =
        SwapKeyRepositoryImpl(apiInterface)

    @Provides
    fun provideTemplateRepository(apiInterface: TemplateApiInterface): ITemplateRepository =
        TemplateRepositoryImpl(apiInterface)

    @Provides
    fun provideUserRepository(apiInterface: UserApiInterface): IUserRepository =
        UserRepositoryImpl(apiInterface)

    @Provides
    fun provideUtilsRepository(apiInterface: UtilsApiInterface): IUtilsRepository =
        UtilsRepositoryImpl(apiInterface)

    @Provides
    fun provideWalletRepository(apiInterface: WalletApiInterface): IWalletRepository =
        WalletRepositoryImpl(apiInterface)

}