package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.applications.ApplicationsResponse
import uz.fido.network.domain.model.applications.GetProductDetailsRequest
import uz.fido.network.domain.model.applications.ProductDetailsResponse
import uz.fido.network.domain.model.cards.OrderCardRequest
import uz.fido.network.domain.model.cards.OrderCardTypeRequest
import uz.fido.network.domain.model.cards.OrderCardTypeResponse
import uz.fido.network.domain.model.cards.OrderVirtualCardRequest
import uz.fido.network.domain.model.fines.*
import uz.fido.network.domain.model.inn.DebtInfoResponse
import uz.fido.network.domain.model.inn.GetDebtByInnRequest
import uz.fido.network.domain.model.inn.GetInnRequest
import uz.fido.network.domain.model.inn.InnInfoResponse
import uz.fido.network.domain.model.invoice.InvoiceListResponse
import uz.fido.network.domain.model.invoice.InvoicePaymentRequest
import uz.fido.network.domain.model.mib.*
import uz.fido.network.domain.model.paid_services.PsServiceResponse
import uz.fido.network.domain.model.paid_services.SetPaidServiceStatusRequest
import uz.fido.network.domain.model.subscriptions.AutoPaymentHistoryRequest
import uz.fido.network.domain.model.subscriptions.AutoPaymentHistoryResponse
import uz.fido.network.domain.model.target.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse

interface ServiceApiInterface {

    @POST("GET_GUBDD_ACCOUNT_LIST")
    suspend fun getGubddAccountList(
        @Header("Authorization") token: String,
        @Body gubddListRequest: GubddListRequest
    ): GubddAccountListResponse

    @POST("GUBDD_REGISTRATION")
    suspend fun gubddRegistration(
        @Header("Authorization") token: String,
        @Body gubddRegisterRequest: GubddRegisterRequest
    ): BaseResponse

    @POST("GUBDD_ACCOUNT_ACTIVATE")
    suspend fun gubddActivate(
        @Header("Authorization") token: String,
        @Body gubddActivateRequest: GubddActivateRequest
    ): BaseResponse

    @POST("DELETE_GUBDD_ACCOUNT")
    suspend fun deleteGubdd(
        @Header("Authorization") token: String,
        @Body deleteGubddRequest: DeleteGubddRequest
    ): BaseResponse

    @POST("GET_CARD_ORDER_TYPES")
    suspend fun getCardOrderTypes(
        @Header("Authorization") token: String,
        @Body orderCardTypeRequest: OrderCardTypeRequest
    ): OrderCardTypeResponse

    @POST("ORDER_CARD")
    suspend fun orderCardRequest(
        @Header("Authorization") token: String,
        @Body orderCardRequest: OrderCardRequest
    ): BaseResponse

    @POST("GET_MIB_INFO")
    suspend fun getMibInfo(
        @Header("Authorization") token: String,
        @Body mibInfoRequest: MibInfoRequest
    ): MibDetailsResponse

    @GET("GET_MIB_PASSPORT_LIST")
    suspend fun getMibPassportList(
        @Header("Authorization") token: String
    ): MibPassportListResponse

    @POST("ADD_MIB_PASSPORT")
    suspend fun addMibPassport(
        @Header("Authorization") token: String,
        @Body addMibPassportRequest: AddMibPassportRequest
    ): BaseResponse

    @POST("DELETE_MIB_ACCOUNT")
    suspend fun deleteMibAccount(
        @Header("Authorization") token: String,
        @Body deleteMibAccount: DeleteMibAccount
    ): BaseResponse

    @GET("GET_USER_E_INVS")
    suspend fun getUserInvoiceList(
        @Header("Authorization") token: String
    ): InvoiceListResponse

    @POST("PAY_TO_E_INVOISE")
    suspend fun invoicePayment(
        @Header("Authorization") token: String,
        @Body invoicePaymentRequest: InvoicePaymentRequest
    ): BaseResponse

    @POST("GET_FULL_INFO_BY_PASSPORT")
    suspend fun getInnRequest(
        @Header("Authorization") token: String,
        @Body getInnRequest: GetInnRequest
    ): InnInfoResponse

    @POST("GET_DEBT_BY_INN")
    suspend fun getDebtsByInnRequest(
        @Header("Authorization") token: String,
        @Body getDebtByInnRequest: GetDebtByInnRequest
    ): DebtInfoResponse

    @GET("GET_USER_PRODUCT_LIST")
    suspend fun getUserProductList(
        @Header("Authorization") token: String
    ): ApplicationsResponse

    @POST("GET_PRODUCT_DETAILS")
    suspend fun getProductDetails(
        @Header("Authorization") token: String,
        @Body getProductDetailsRequest: GetProductDetailsRequest
    ): ProductDetailsResponse

    @POST("ORDER_CARD_VIRTUAL")
    suspend fun orderVirtualCard(
        @Header("Authorization") token: String,
        @Body orderVirtualCardRequest: OrderVirtualCardRequest
    ): BaseResponse

    @GET("GET_PS_SERVICE_TYPES")
    suspend fun getPsServices(
        @Header("Authorization") token: String
    ): PsServiceResponse

    @POST("PS_SET_USER_SERVICE")
    suspend fun setStateOfPs(
        @Header("Authorization") token: String,
        @Body setPaidServiceStatus: SetPaidServiceStatusRequest
    ): BaseResponse

    @POST("TR_SET_TARGET")
    suspend fun setTarget(
        @Header("Authorization") token: String,
        @Body request: SetTargetRequest
    ): BaseResponse

    @GET("TR_GET_USER_TARGET_LIST")
    suspend fun getTargetList(
        @Header("Authorization") token: String
    ): GoalListResponse

    @POST("GET_AUTO_PAYMENT_OPER_LIST")
    suspend fun autoPaymentHistory(
        @Header("Authorization") token: String,
        @Body autoPaymentHistoryRequest: AutoPaymentHistoryRequest
    ): AutoPaymentHistoryResponse

    @POST("TR_CHANGE_TARGET_STATE")
    suspend fun changeTargetState(
        @Header("Authorization") token: String,
        @Body request: ChangeTargetStateRequest
    ): BaseResponse

    @POST("TR_EDIT_TARGET")
    suspend fun editGoal(
        @Header("Authorization") token: String,
        @Body request: EditGoalRequest
    ): BaseResponse

    @POST("TR_GET_USER_TARGET_TRANSFER")
    suspend fun targetHistories(
        @Header("Authorization") token: String,
        @Body request: ChangeTargetStateRequest
    ): GoalHistoriesResponse

    @POST("TR_GET_USER_TARGET_INFO")
    suspend fun getTargetInfo(
        @Header("Authorization") token: String,
        @Body request: ChangeTargetStateRequest
    ): GoalModelResponse

}