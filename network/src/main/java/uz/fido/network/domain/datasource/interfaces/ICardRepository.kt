package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.cards.AddCardRequest
import uz.fido.network.domain.model.cards.AddCheckCardResponse
import uz.fido.network.domain.model.cards.BlockCardRequest
import uz.fido.network.domain.model.cards.BlockCardResponse
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardInfoResponse
import uz.fido.network.domain.model.cards.CardListResponse
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
import uz.fido.network.domain.model.cards.GetObjValueRequest
import uz.fido.network.domain.model.cards.GetObjValueResponse
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

interface ICardRepository {

    suspend fun checkCard(
        token: String, checkCardRequest: CheckCardRequest
    ): Resource<AddCheckCardResponse>

    suspend fun addCard(token: String, addCardRequest: AddCardRequest): Resource<BaseResponse>

    suspend fun getCardList(token: String): Resource<CardListResponse>
    suspend fun getObjValue(token: String,request: GetObjValueRequest): Resource<GetObjValueResponse>

    suspend fun getCardInfo(
        token: String, cardInfoRequest: CardInfoRequest
    ): Resource<CardInfoResponse>

    suspend fun deleteCard(
        token: String, deleteCardRequest: DeleteCardRequest
    ): Resource<DeleteCardResponse>

    suspend fun editCard(
        token: String, editCardRequest: EditCardRequest
    ): Resource<EditCardResponse>

    suspend fun blockCard(
        token: String, blockCardRequest: BlockCardRequest
    ): Resource<BlockCardResponse>

    suspend fun unblockCard(
        token: String, blockCardRequest: BlockCardRequest
    ): Resource<BlockCardResponse>

    suspend fun getCardByPhone(
        token: String, getCardByPhoneRequest: GetCardByPhoneRequest
    ): Resource<CardByPhoneResponse>

    suspend fun checkCardInfo(
        token: String, checkCardRequestP2p: CheckCardRequestP2p
    ): Resource<CheckCardResponse>

    suspend fun checkWalletInfo(
        token: String, checkCardRequestP2p: CheckCardRequestP2p
    ): Resource<CheckWalletResponse>

    suspend fun getHumoCardInfo(
        token: String, humoCardInfoRequest: HumoCardInfoRequest
    ): Resource<NfcHUMOInfoResponse>

    suspend fun checkResetPinCount(
        token: String, resetPinCountCheck: ResetPinCountCheck
    ): Resource<BaseResponse>

    suspend fun resetPinCount(token: String, resetPinCount: ResetPinCount): Resource<BaseResponse>

    suspend fun getSvCardLimitList(
        token: String, cardLimitRequest: CardLimitRequest
    ): Resource<SvLimitResponse>

    suspend fun deleteSvCardLimit(
        token: String, limitDeleteRequest: LimitDeleteRequest
    ): Resource<BaseResponse>

    suspend fun deleteGlCardLimit(
        token: String, limitDeleteRequest: GlLimitDeleteRequest
    ): Resource<BaseResponse>

    suspend fun svSetMainCard(
        token: String, svSetMainCardRequest: SvSetMainCardRequest
    ): Resource<BaseResponse>

    suspend fun getSvLimitParams(token: String): Resource<LimitParamsResponse>

    suspend fun setSvCardLimit(
        token: String, svSetCardLimit: SvSetCardLimitRequest
    ): Resource<BaseResponse>

    suspend fun setGlCardLimit(
        token: String, glSetCardLimitRequest: GlSetCardLimitRequest
    ): Resource<BaseResponse>

    suspend fun getGlLimitList(
        token: String, glLimitListRequest: GlLimitListRequest
    ): Resource<GlLimitResponse>

    suspend fun getGlLimitBalance(
        token: String, glLimitBaseRequest: GlLimitBaseRequest
    ): Resource<BaseResponse>

    suspend fun getGlLimitParams(token: String): Resource<GlLimitParamsResponse>

    suspend fun checkSMSActivate(
        token: String, request: CheckSMSActivateRequest
    ): Resource<BaseResponse>

    suspend fun glSMSActivate(token: String, request: GlSMSActivateRequest): Resource<BaseResponse>
    suspend fun secure3DAction(token: String, request: Secure3DRequest): Resource<secure3DResponse>
    suspend fun getCVV(token: String, request: GetCVVRequest): Resource<GetCVVResponse>
}