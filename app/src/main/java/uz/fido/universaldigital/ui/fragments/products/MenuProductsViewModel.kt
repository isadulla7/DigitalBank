package uz.fido.universaldigital.ui.fragments.products

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.fido.network.data.repository.CardRepositoryImpl
import uz.fido.network.domain.datasource.interfaces.ICreditRepository
import uz.fido.network.domain.datasource.interfaces.IDepositRepository
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.datasource.interfaces.IWalletRepository
import uz.fido.network.domain.model.cards.BlockCardRequest
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.CheckCardRequest
import uz.fido.network.domain.model.cards.DeleteCardRequest
import uz.fido.network.domain.model.cards.EditCardRequest
import uz.fido.network.domain.model.cards.GetCVVRequest
import uz.fido.network.domain.model.cards.GetObjValueRequest
import uz.fido.network.domain.model.cards.Secure3DRequest
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.limits.CardLimitRequest
import uz.fido.network.domain.model.limits.LimitDeleteRequest
import uz.fido.network.domain.model.limits.SvSetCardLimitRequest
import uz.fido.network.domain.model.limits.gl.GlLimitDeleteRequest
import uz.fido.network.domain.model.limits.gl.GlLimitListRequest
import uz.fido.network.domain.model.limits.gl.GlSetCardLimitRequest
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.news.Notification
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuProductsViewModel @Inject constructor(
    application: Application,
    private val cardRepository: CardRepositoryImpl,
    private val p2PRepository: IP2PRepository,
    private val walletRepository: IWalletRepository,
    private val depositRepository: IDepositRepository,
    private val creditRepository: ICreditRepository,
    private val utilsRepository: IUtilsRepository,
) : AbstractViewModel(application) {

    var cards: LiveData<List<CardResponse>> = cardRepository.cardList
    var isCardPasted: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _notification = MutableStateFlow<ArrayList<Notification>>(arrayListOf())
    val notification: StateFlow<ArrayList<Notification>> = _notification

    var updateCardState: MutableLiveData<Boolean> = MutableLiveData()
    var creditProduct: MutableLiveData<List<CreditProduct>> = MutableLiveData()
    var clientDeposit: MutableLiveData<ArrayList<ClientDeposit>> = MutableLiveData()
    var depositProducts: MutableLiveData<List<Deposit>> = MutableLiveData()
    var shouldUpdate = false

    init {
        updateCardState.postValue(false)
    }

    fun setNotificationList(list: ArrayList<Notification>) {
        _notification.value = list
    }

    fun updateClientDepositList(list: ArrayList<ClientDeposit>) {
        this.clientDeposit.postValue(list)
    }

    fun updateCards(cards: List<CardResponse>) {
        cardRepository.updateCards(cards)
    }

    fun updateCreditGroups(creditProduct: List<CreditProduct>) {
        this.creditProduct.postValue(creditProduct)
    }

    fun updateDepositProducts(depositProducts: List<Deposit>) {
        this.depositProducts.postValue(depositProducts)
    }

    fun getCardListRequest(token: String) = liveData(Dispatchers.IO) {
        emit(cardRepository.getCardList(token))
    }

    fun getCardNumberRequest(token: String, request: GetObjValueRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.getObjValue(token, request))
    }

    fun getCardInfoRequest(token: String, cardInfoRequest: CardInfoRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.getCardInfo(token, cardInfoRequest))
    }

    fun checkCardRequest(token: String, checkCardRequest: CheckCardRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.checkCard(token, checkCardRequest))
    }

    fun editCardRequest(token: String, editCardRequest: EditCardRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.editCard(token, editCardRequest))
    }

    fun secure3DAction(token: String, secure3DRequest: Secure3DRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.secure3DAction(token, secure3DRequest))
    }

    fun getCVV(token: String, request: GetCVVRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.getCVV(token, request))
    }

    fun getSvCardLimitList(token: String, request: CardLimitRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.getSvCardLimitList(token, request))
    }

    fun getGlCardLimitList(token: String, request: GlLimitListRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.getGlLimitList(token, request))
    }

    fun getGlLimitParams(clientToken: String) = liveData(Dispatchers.IO) {
        emit(cardRepository.getGlLimitParams(clientToken))
    }

    fun getSvLimitParams(clientToken: String) = liveData(Dispatchers.IO) {
        emit(cardRepository.getSvLimitParams(clientToken))
    }

    fun setGlCardLimit(clientToken: String, request: GlSetCardLimitRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.setGlCardLimit(clientToken, request))
    }

    fun setSvCardLimit(clientToken: String, request: SvSetCardLimitRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.setSvCardLimit(clientToken, request))
    }

    fun deleteSvCardLimit(clientToken: String, request: LimitDeleteRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.deleteSvCardLimit(clientToken, request))
    }

    fun deleteGlCardLimit(clientToken: String, request: GlLimitDeleteRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.deleteGlCardLimit(clientToken, request))
    }

    fun deleteCardRequest(token: String, deleteCardRequest: DeleteCardRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.deleteCard(token, deleteCardRequest))
    }

    fun deleteWallet(token: String, deleteWalletRequest: DeleteWalletRequest) = liveData(Dispatchers.IO) {
        emit(walletRepository.deleteWallet(token, deleteWalletRequest))
    }

    fun blockCardRequest(token: String, blockCardRequest: BlockCardRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.blockCard(token, blockCardRequest))
    }

    fun getClientDepositList(token: String) = liveData(Dispatchers.IO) {
        emit(depositRepository.getClientDepositList(token))
    }

    fun getClientProducts(token: String) = liveData(Dispatchers.IO) {
        emit(creditRepository.getCreditProducts(token))
    }

    fun getDeposits(token: String, getDepositListRequest: GetDepositListRequest) = liveData(Dispatchers.IO) {
        emit(depositRepository.getDeposits(token, getDepositListRequest))
    }

    fun getNotifications(token: String, request: GetNotificationsRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getNotifications(token, request))
    }

    fun updateNotificationStatus(token: String, updateNewsStatusRequest: UpdateNotificationState) =
        liveData(Dispatchers.IO) {
            emit(utilsRepository.updateNotificationStatus(token, updateNewsStatusRequest))
        }

}