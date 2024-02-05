package uz.fido.network.data.repository

import androidx.lifecycle.MutableLiveData
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.datasource.services.CardApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.cards.AddCardRequest
import uz.fido.network.domain.model.cards.AddCheckCardResponse
import uz.fido.network.domain.model.cards.BlockCardRequest
import uz.fido.network.domain.model.cards.BlockCardResponse
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardInfoResponse
import uz.fido.network.domain.model.cards.CardListResponse
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.CheckCardRequest
import uz.fido.network.domain.model.cards.CheckCardRequestP2p
import uz.fido.network.domain.model.cards.CheckCardResponse
import uz.fido.network.domain.model.cards.CheckWalletResponse
import uz.fido.network.domain.model.cards.DeleteCardRequest
import uz.fido.network.domain.model.cards.DeleteCardResponse
import uz.fido.network.domain.model.cards.EditCardRequest
import uz.fido.network.domain.model.cards.EditCardResponse
import uz.fido.network.domain.model.cards.GetCVVRequest
import uz.fido.network.domain.model.cards.GetCVVResponse
import uz.fido.network.domain.model.cards.ResetPinCount
import uz.fido.network.domain.model.cards.ResetPinCountCheck
import uz.fido.network.domain.model.cards.Secure3DRequest
import uz.fido.network.domain.model.cards.secure3DResponse
import uz.fido.network.domain.model.get_card_by_phone.CardByPhoneResponse
import uz.fido.network.domain.model.get_card_by_phone.GetCardByPhoneRequest
import uz.fido.network.domain.model.home.CheckSMSActivateRequest
import uz.fido.network.domain.model.home.GlSMSActivateRequest
import uz.fido.network.domain.model.humo_pay.HumoCardInfoRequest
import uz.fido.network.domain.model.humo_pay.NfcHUMOInfoResponse
import uz.fido.network.domain.model.limits.CardLimitRequest
import uz.fido.network.domain.model.limits.LimitDeleteRequest
import uz.fido.network.domain.model.limits.LimitParamsResponse
import uz.fido.network.domain.model.limits.SvLimitResponse
import uz.fido.network.domain.model.limits.SvSetCardLimitRequest
import uz.fido.network.domain.model.limits.SvSetMainCardRequest
import uz.fido.network.domain.model.limits.gl.GlLimitBaseRequest
import uz.fido.network.domain.model.limits.gl.GlLimitDeleteRequest
import uz.fido.network.domain.model.limits.gl.GlLimitListRequest
import uz.fido.network.domain.model.limits.gl.GlLimitParamsResponse
import uz.fido.network.domain.model.limits.gl.GlLimitResponse
import uz.fido.network.domain.model.limits.gl.GlSetCardLimitRequest
import uz.fido.network.domain.model.p2p.P2PHistoryRequest
import uz.fido.network.domain.model.p2p.P2PHistoryResponse
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(private val cardApiService: CardApiInterface) :
    ICardRepository {

    var cardList: MutableLiveData<List<CardResponse>> = MutableLiveData()

    fun updateCards(cards: List<CardResponse>) {
        this.cardList.postValue(cards)
    }

    override suspend fun checkCard(
        token: String, checkCardRequest: CheckCardRequest
    ): Resource<AddCheckCardResponse> = getResult {
        cardApiService.checkCard(token, checkCardRequest)
    }

    override suspend fun addCard(
        token: String, addCardRequest: AddCardRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.addCard(token, addCardRequest)
    }

    override suspend fun getCardList(token: String): Resource<CardListResponse> = getResult {
        cardApiService.getCardList(token)
    }

    override suspend fun getCardInfo(
        token: String, cardInfoRequest: CardInfoRequest
    ): Resource<CardInfoResponse> = getResult {
        cardApiService.getCardInfo(token, cardInfoRequest)
    }

    override suspend fun deleteCard(
        token: String, deleteCardRequest: DeleteCardRequest
    ): Resource<DeleteCardResponse> = getResult {
        cardApiService.deleteCard(token, deleteCardRequest)
    }

    override suspend fun editCard(
        token: String, editCardRequest: EditCardRequest
    ): Resource<EditCardResponse> = getResult {
        cardApiService.editCard(token, editCardRequest)
    }

    override suspend fun blockCard(
        token: String, blockCardRequest: BlockCardRequest
    ): Resource<BlockCardResponse> = getResult {
        cardApiService.blockCard(token, blockCardRequest)
    }

    override suspend fun unblockCard(
        token: String, blockCardRequest: BlockCardRequest
    ): Resource<BlockCardResponse> = getResult {
        cardApiService.unblockCard(token, blockCardRequest)
    }

    override suspend fun getCardByPhone(
        token: String, getCardByPhoneRequest: GetCardByPhoneRequest
    ): Resource<CardByPhoneResponse> = getResult {
        cardApiService.getCardByPhone(token, getCardByPhoneRequest)
    }

    override suspend fun checkCardInfo(
        token: String, checkCardRequestP2p: CheckCardRequestP2p
    ): Resource<CheckCardResponse> = getResult {
        cardApiService.checkCardInfo(token, checkCardRequestP2p)
    }

    override suspend fun checkWalletInfo(
        token: String, checkCardRequestP2p: CheckCardRequestP2p
    ): Resource<CheckWalletResponse> = getResult {
        cardApiService.checkWalletInfo(token, checkCardRequestP2p)
    }

    override suspend fun getP2pHistory(
        token: String, p2PHistoryRequest: P2PHistoryRequest
    ): Resource<P2PHistoryResponse> = getResult {
        cardApiService.getP2pHistory(token, p2PHistoryRequest)
    }

    override suspend fun getHumoCardInfo(
        token: String, humoCardInfoRequest: HumoCardInfoRequest
    ): Resource<NfcHUMOInfoResponse> = getResult {
        cardApiService.getHumoCardInfo(token, humoCardInfoRequest)
    }

    override suspend fun checkResetPinCount(
        token: String, resetPinCountCheck: ResetPinCountCheck
    ): Resource<BaseResponse> = getResult {
        cardApiService.checkResetPinCount(token, resetPinCountCheck)
    }

    override suspend fun resetPinCount(
        token: String, resetPinCount: ResetPinCount
    ): Resource<BaseResponse> = getResult {
        cardApiService.resetPinCount(token, resetPinCount)
    }

    override suspend fun getSvCardLimitList(
        token: String, cardLimitRequest: CardLimitRequest
    ): Resource<SvLimitResponse> = getResult {
        cardApiService.getSvCardLimitList(token, cardLimitRequest)
    }

    override suspend fun deleteSvCardLimit(
        token: String, limitDeleteRequest: LimitDeleteRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.deleteSvCardLimit(token, limitDeleteRequest)
    }

    override suspend fun deleteGlCardLimit(
        token: String, limitDeleteRequest: GlLimitDeleteRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.glLimitDelete(token, limitDeleteRequest)
    }

    override suspend fun svSetMainCard(
        token: String, svSetMainCardRequest: SvSetMainCardRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.svSetMainCard(token, svSetMainCardRequest)
    }

    override suspend fun getSvLimitParams(token: String): Resource<LimitParamsResponse> =
        getResult {
            cardApiService.getSvLimitParams(token)
        }

    override suspend fun setSvCardLimit(
        token: String, svSetCardLimit: SvSetCardLimitRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.setSvCardLimit(token, svSetCardLimit)
    }

    override suspend fun setGlCardLimit(
        token: String, glSetCardLimitRequest: GlSetCardLimitRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.setGlCardLimit(token, glSetCardLimitRequest)
    }

    override suspend fun getGlLimitList(
        token: String, glLimitListRequest: GlLimitListRequest
    ): Resource<GlLimitResponse> = getResult {
        cardApiService.getGlLimitList2(token, glLimitListRequest)
    }

    override suspend fun getGlLimitBalance(
        token: String, glLimitBaseRequest: GlLimitBaseRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.getGlLimitBalance(token, glLimitBaseRequest)
    }

    override suspend fun getGlLimitParams(token: String): Resource<GlLimitParamsResponse> =
        getResult {
            cardApiService.getGlLimitParams(token)
        }

    override suspend fun checkSMSActivate(
        token: String, request: CheckSMSActivateRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.checkSMSActivate(token, request)
    }

    override suspend fun glSMSActivate(
        token: String, request: GlSMSActivateRequest
    ): Resource<BaseResponse> = getResult {
        cardApiService.glSMSActivate(token, request)
    }

    override suspend fun secure3DAction(
        token: String, request: Secure3DRequest
    ): Resource<secure3DResponse> = getResult {
        cardApiService.secure3DAction(token, request)
    }

    override suspend fun getCVV(
        token: String, request: GetCVVRequest
    ): Resource<GetCVVResponse> = getResult {
        cardApiService.getCVV(token, request)
    }

}