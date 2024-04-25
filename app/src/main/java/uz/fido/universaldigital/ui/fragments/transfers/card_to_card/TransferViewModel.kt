package uz.fido.universaldigital.ui.fragments.transfers.card_to_card

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.get_card_by_phone.CardByPhone
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.universaldigital.ui.fragments.transfers.utils.getInfoCommand
import uz.fido.universaldigital.ui.fragments.transfers.utils.getServiceIdInfo
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.Command
import uz.fido.utils.utility.activity.LiveEvent
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    application: Application,
    private val useCase: TransferToCardUseCase
) : AbstractViewModel(application) {

    var popularTransfers = LiveEvent<ArrayList<PopularTransfers>>()
    var cardInfo = LiveEvent<CardInfoDto>()
    var p2pInfo = LiveEvent<P2PInfoDto>()
    var popularTransfersLoader = LiveEvent<Boolean>()
    var historiesByPhoneNumber = LiveEvent<ArrayList<CardByPhone>>()
    var historiesByWalletNumber = LiveEvent<ArrayList<CardByPhone>>()
    var setToPopularTransfer = LiveEvent<ArrayList<PopularTransfers>>()

    fun getPopularTransfers() {
        vmScope.launch {
            popularTransfersLoader.postValue(true)
            val result = useCase.getPopularTransferList()
            popularTransfers.postValue(result)
            popularTransfersLoader.postValue(false)
        }
    }

    fun getFavoriteTransfers() {
        vmScope.launch {
            popularTransfersLoader.postValue(true)
            val result = useCase.getPopularTransferList()
            val sortedList = result.filter { it.is_favourite == "Y" }
            popularTransfers.postValue(sortedList as ArrayList<PopularTransfers>?)
            popularTransfersLoader.postValue(false)

        }
    }

    fun getCardInfo(cardNumber: String, objectId: String? = null) {
        vmScope.launch {
            val result =
                useCase.getCardInfo(CheckCardRequestP2p(Command.CARD, cardNumber, objectId))
            cardInfo.postValue(result)
        }
    }

    fun getWalletInfo(cardNumber: String) {
        vmScope.launch {
            val result = useCase.getCardInfo(CheckCardRequestP2p(Command.PURSE, cardNumber))
            cardInfo.postValue(result)
        }
    }

    fun getCardInfoByPhone(phoneNumber: String) {
        vmScope.launch {
            val result = useCase.getCardInfo(CheckCardRequestP2p(Command.INFO, phoneNumber))
            cardInfo.postValue(result)
        }
    }

    fun getTransferInfo(senderCard: CardResponse?, receiverCardDto: CardInfoDto?) {
        if (senderCard != null && receiverCardDto != null) {
            if (senderCard.object_value != receiverCardDto.card_number) {
                vmScope.launch {
                    val result = useCase.getTransferInfo(
                        P2PInfoRequest(
                            service_id = getServiceIdInfo(
                                receiverCardDto.card_number?:"",
                                senderCard.object_value
                            ),
                            from_object_id = senderCard.object_id,
                            expire = senderCard.object_expiry,
                            to_object_value = receiverCardDto.card_number?:"",
                            to_object_id = receiverCardDto.card_id,
                            command = getInfoCommand(receiverCardDto.card_number?:"")
                        )
                    )
                    p2pInfo.postValue(result)
                }
            }
        }
    }

    fun getHistoriesByPhone() {
        vmScope.launch {
            popularTransfersLoader.postValue(true)
            val result = ArrayList<CardByPhone>()
            val response = useCase.getTransferHistories()
            response.forEach {
                if (it.phone_number.isNotEmpty()) {
                    result.add(it)
                }
            }
            popularTransfersLoader.postValue(false)
            historiesByPhoneNumber.postValue(result)
        }
    }

    fun getHistoriesByWallet() {
        vmScope.launch {
            val result = ArrayList<CardByPhone>()
            val response = useCase.getTransferHistories()
            response.forEach {
                if (it.card_type == CardConst.WALLET) {
                    result.add(it)
                }
            }
            historiesByWalletNumber.postValue(result)
        }
    }

    fun setToPopularTransfer(objectValue: String) {
        vmScope.launch {
            val response = useCase.setToPopularTransfer(objectValue)
            setToPopularTransfer.postValue(response)
        }
    }

}