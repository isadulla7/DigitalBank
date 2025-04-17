package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.cards.*
import uz.fido.network.domain.model.get_card_by_phone.CardByPhoneResponse
import uz.fido.network.domain.model.get_card_by_phone.GetCardByPhoneRequest
import uz.fido.network.domain.model.home.CheckSMSActivateRequest
import uz.fido.network.domain.model.home.GlSMSActivateRequest
import uz.fido.network.domain.model.humo_pay.HumoCardInfoRequest
import uz.fido.network.domain.model.humo_pay.NfcHUMOInfoResponse
import uz.fido.network.domain.model.limits.*
import uz.fido.network.domain.model.limits.gl.GlLimitBaseRequest
import uz.fido.network.domain.model.limits.gl.GlLimitDeleteRequest
import uz.fido.network.domain.model.limits.gl.GlLimitListRequest
import uz.fido.network.domain.model.limits.gl.GlLimitParamsResponse
import uz.fido.network.domain.model.limits.gl.GlLimitResponse
import uz.fido.network.domain.model.limits.gl.GlSetCardLimitRequest
import uz.fido.network.domain.model.p2p.P2PHistoryRequest
import uz.fido.network.domain.model.p2p.P2PHistoryResponse

interface CardApiInterface {

    @POST("USER_OBJ_CHECK")
    suspend fun checkCard(
        @Header("Authorization") token: String,
        @Body checkCardRequest: CheckCardRequest
    ): AddCheckCardResponse

    @POST("USER_OBJ_ADD")
    suspend fun addCard(
        @Header("Authorization") token: String,
        @Body addCardRequest: AddCardRequest
    ): BaseResponse

    @GET("USER_OBJ_LIST")
    suspend fun getCardList(
        @Header("Authorization") token: String
    ): CardListResponse

    @POST("USER_OBJ_INFO_NEW")
    suspend fun getCardInfo(
        @Header("Authorization") token: String,
        @Body cardInfoRequest: CardInfoRequest
    ): CardInfoResponse

    @POST("GET_REAL_OBJECT_VALUE")
    suspend fun getObjectRealValue(
        @Header("Authorization") token: String,
        @Body getObjValue: GetObjValueRequest
    ): GetObjValueResponse

    @POST("USER_OBJ_DEL")
    suspend fun deleteCard(
        @Header("Authorization") token: String,
        @Body deleteCardRequest: DeleteCardRequest
    ): DeleteCardResponse

    @POST("USER_OBJ_EDIT")
    suspend fun editCard(
        @Header("Authorization") token: String,
        @Body editCardRequest: EditCardRequest
    ): EditCardResponse

    @POST("BLOCK_CARD")
    suspend fun blockCard(
        @Header("Authorization") token: String,
        @Body blockCardRequest: BlockCardRequest
    ): BlockCardResponse

    @POST("UNBLOCK_CARD")
    suspend fun unblockCard(
        @Header("Authorization") token: String,
        @Body blockCardRequest: BlockCardRequest
    ): BlockCardResponse

    @POST("GET_USER_OBJ_BY_PHONE")
    suspend fun getCardByPhone(
        @Header("Authorization") token: String,
        @Body getCardByPhoneRequest: GetCardByPhoneRequest
    ): CardByPhoneResponse

    @POST("CARD_INFO")
    suspend fun checkCardInfo(
        @Header("Authorization") token: String,
        @Body checkCardRequestP2p: CheckCardRequestP2p
    ): CheckCardResponse

    @POST("CARD_INFO")
    suspend fun checkWalletInfo(
        @Header("Authorization") token: String,
        @Body checkCardRequestP2p: CheckCardRequestP2p
    ): CheckWalletResponse

    @POST("GET_HUMO_CARD_INFO")
    suspend fun getHumoCardInfo(
        @Header("Authorization") token: String,
        @Body humoCardInfoRequest: HumoCardInfoRequest
    ): NfcHUMOInfoResponse

    @POST("CHECK_RESET_PIN_COUNTER")
    suspend fun checkResetPinCount(
        @Header("Authorization") token: String,
        @Body resetPinCountCheck: ResetPinCountCheck
    ): BaseResponse

    @POST("RESET_PIN_COUNTER")
    suspend fun resetPinCount(
        @Header("Authorization") token: String,
        @Body resetPinCount: ResetPinCount
    ): BaseResponse

    @POST("SV_GET_CARD_LIMITS")
    suspend fun getSvCardLimitList(
        @Header("Authorization") token: String,
        @Body cardLimitRequest: CardLimitRequest
    ): SvLimitResponse

    @POST("SV_DELETE_CARD_LIMIT")
    suspend fun deleteSvCardLimit(
        @Header("Authorization") token: String,
        @Body limitDeleteRequest: LimitDeleteRequest
    ): BaseResponse

    @POST("GL_DELETE_CARD_LIMIT")
    suspend fun glLimitDelete(
        @Header("Authorization") token: String,
        @Body glLimitBaseRequest: GlLimitDeleteRequest
    ): BaseResponse


    @POST("SV_SET_MAIN_CARD")
    suspend fun svSetMainCard(
        @Header("Authorization") token: String,
        @Body svSetMainCardRequest: SvSetMainCardRequest
    ): BaseResponse

    @GET("GET_SV_GATE_LIMIT_PARAMS")
    suspend fun getSvLimitParams(
        @Header("Authorization") token: String
    ): LimitParamsResponse

    @POST("SV_SET_CARD_LIMIT")
    suspend fun setSvCardLimit(
        @Header("Authorization") token: String,
        @Body svSetCardLimit: SvSetCardLimitRequest
    ): BaseResponse

    @POST("GL_SET_LIMIT")
    suspend fun setGlCardLimit(
        @Header("Authorization") token: String,
        @Body glSetCardLimitRequest: GlSetCardLimitRequest
    ): BaseResponse

    @POST("GL_GET_LIMITS_BALANCE")
    suspend fun getGlLimitBalance(
        @Header("Authorization") token: String,
        @Body glLimitBaseRequest: GlLimitBaseRequest
    ): BaseResponse

    @POST("GL_GET_LIMIT")
    suspend fun getGlLimitList2(
        @Header("Authorization") token: String,
        @Body glLimitListRequest: GlLimitListRequest
    ): GlLimitResponse

    @POST("CHECK_GL_SMS_ACTIVATE")
    suspend fun checkSMSActivate(
        @Header("Authorization") token: String, @Body request: CheckSMSActivateRequest
    ): BaseResponse

    @POST("GL_SMS_ACTIVATE")
    suspend fun glSMSActivate(
        @Header("Authorization") token: String, @Body request: GlSMSActivateRequest
    ): BaseResponse

    @POST("3D_SECURE_ACTION")
    suspend fun secure3DAction(
        @Header("Authorization") token: String, @Body request: Secure3DRequest
    ): secure3DResponse

    @POST("TET_GET_SECURITY_CODE")
    suspend fun getCVV(
        @Header("Authorization") token: String,
        @Body cardNumber: GetCVVRequest
    ): GetCVVResponse

    @GET("GET_HUMO_LIMIT_PARAMS")
    suspend fun getGlLimitParams(
        @Header("Authorization") token: String
    ): GlLimitParamsResponse

}