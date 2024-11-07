package uz.fido.universaldigital.base

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.fido.network.data.repository.CardRepositoryImpl
import uz.fido.network.data.repository.P2PRepositoryImpl
import uz.fido.network.data.repository.UtilsRepositoryImpl
import uz.fido.universaldigital.ui.fragments.products.CardsUseCase
import uz.fido.universaldigital.ui.fragments.products.CardsUseCaseImpl
import uz.fido.universaldigital.ui.fragments.products.widgets.notifications.NotificationUseCase
import uz.fido.universaldigital.ui.fragments.products.widgets.notifications.NotificationUseCaseImpl
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferToCardUseCase
import uz.fido.universaldigital.ui.fragments.transfers.card_to_card.TransferToCardUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
object UseCaseProvider {

    @Provides
    fun provideTransferUseCse(repository: P2PRepositoryImpl): TransferToCardUseCase =
        TransferToCardUseCaseImpl(repository)

    @Provides
    fun provideNotificationsUseCase(utilsRepositoryImpl: UtilsRepositoryImpl): NotificationUseCase =
        NotificationUseCaseImpl()

    @Provides
    fun provideCardsUseCase(cardsRepositoryImpl: CardRepositoryImpl, utilsRepositoryImpl: UtilsRepositoryImpl): CardsUseCase =
        CardsUseCaseImpl(utilsRepositoryImpl)

}