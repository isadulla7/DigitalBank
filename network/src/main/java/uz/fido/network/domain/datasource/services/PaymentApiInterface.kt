package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.branches.BankNameResponse
import uz.fido.network.domain.model.branches.GetBankNameRequest
import uz.fido.network.domain.model.branches.OneTimeInfoRequest
import uz.fido.network.domain.model.branches.OneTimeInfoResponse
import uz.fido.network.domain.model.humo_pay.HumoPayRequest
import uz.fido.network.domain.model.humo_pay.NfcResponse
import uz.fido.network.domain.model.payment.*
import uz.fido.network.domain.model.payment.location.LocalPaymentRequest
import uz.fido.network.domain.model.payment.location.LocalPaymentTypesResponse
import uz.fido.network.domain.model.payment.location.PaymentByLocationRequest
import uz.fido.network.domain.model.payment.location.PaymentByLocationResponse
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.network.domain.model.subscriptions.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.InParamsResponse

interface PaymentApiInterface {

    @POST("PREPARE_PAYMENT")
    suspend fun preparePayment(
        @Header("Authorization") token: String,
        @Body preparePaymentRequest: PreparePaymentRequest
    ): PreparePaymentResponse

    @POST(".")
    suspend fun createPayment(
        @Header("Authorization") token: String,
        @Body createPaymentRequest: CreatePaymentRequest
    ): CreatePaymentResponse

    @POST("LOAN_REPAYMENT")
    suspend fun loanRepayment(
        @Header("Authorization") token: String,
        @Body createPaymentRequest: CreatePaymentRequest
    ): CreatePaymentResponse

    @POST("GET_LOCAL_PAY_ONSPOT_LIST")
    suspend fun fetchLocalPayments(
        @Header("Authorization") token: String,
        @Body paymentByLocationRequest: PaymentByLocationRequest
    ): PaymentByLocationResponse

    @GET("GET_LOCAL_PAYMENT_TYPES")
    suspend fun fetchLocalPaymentTypes(
        @Header("Authorization") token: String
    ): LocalPaymentTypesResponse

    @POST("GET_PAYMENTS_VERSION")
    suspend fun getPaymentVersion(
        @Header("Authorization") token: String,
        @Body getPaymentVersionRequest: GetPaymentVersionRequest
    ): GetPaymentVersionResponse

    @POST("LOAN_REPAYMENT")
    suspend fun loadRepayment(
        @Header("Authorization") token: String,
        @Body createPaymentRequest: CreatePaymentRequest
    ): BaseResponse

    @POST("PAY_TO_ONSPOT")
    suspend fun localPayment(
        @Header("Authorization") token: String,
        @Body localPaymentRequest: LocalPaymentRequest
    ): BaseResponse

    @POST("PRINT_CHEQUE")
    suspend fun printCheque(
        @Header("Authorization") token: String,
        @Body chequeRequest: PrintChequeRequest
    ): PrintChequeResponse

    @POST("HUMOPAY")
    suspend fun humoPay(
        @Header("Authorization") token: String,
        @Body humoPayRequest: HumoPayRequest
    ): Call<NfcResponse>

    @POST("GET_AUTO_PAYMENT_LIST")
    suspend fun getAutoPaymentList(
        @Header("Authorization") token: String,
        @Body autoPaymentRequest: AutoPaymentRequest
    ): AutoPaymentResponse

    @POST("SAVE_AUTO_PAYMENT")
    suspend fun createAutoPayment(
        @Header("Authorization") token: String,
        @Body saveAutoPaymentModel: SaveAutoPaymentModel
    ): BaseResponse

    @POST("DELETE_AUTO_PAYMENT")
    suspend fun deleteAutoPayment(
        @Header("Authorization") token: String,
        @Body deleteAutoPaymentRequest: DeleteAutoPaymentRequest
    ): BaseResponse

    @POST("SET_STATE_AUTO_PAYMENT")
    suspend fun changeAutoPaymentState(
        @Header("Authorization") token: String,
        @Body changeAutoPaymentStateRequest: ChangeAutoPaymentStateRequest
    ): BaseResponse

    @POST("EDIT_AUTO_PAYMENT")
    suspend fun editAutoPayment(
        @Header("Authorization") token: String,
        @Body saveAutoPaymentModel: SaveAutoPaymentModel
    ): BaseResponse

    @POST("CHECK_SMS_FOR_PAYMENT")
    suspend fun checkSmsForPayment(
        @Header("Authorization") token: String,
        @Body checkSmsForPayment: CheckSmsForPayment
    ): BaseResponse

    @POST("GET_REQUEST_IN_PARAMS")
    suspend fun getOperationParams(
        @Header("Authorization") token: String,
        @Body getOperationParamRequest: GetOperationInfoRequest
    ): InParamsResponse

    @POST("GET_BANK_NAME")
    suspend fun getBankName(
        @Header("Authorization") token: String,
        @Body getBankNameRequest: GetBankNameRequest
    ): BankNameResponse

    @POST("GET_ONE_TIME_PAY_INFO")
    suspend fun getOneTimePayInfo(
        @Header("Authorization") token: String,
        @Body oneTimeInfoRequest: OneTimeInfoRequest
    ): OneTimeInfoResponse

}