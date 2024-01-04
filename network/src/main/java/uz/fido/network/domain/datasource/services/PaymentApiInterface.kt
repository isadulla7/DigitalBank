package uz.fido.network.domain.datasource.services

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.InParamsResponse
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
import uz.fido.network.domain.model.sms.CheckSmsForPaymentResponse
import uz.fido.network.domain.model.subscriptions.*
import uz.fido.network.domain.model.swift.CreateSwiftAppRequest
import uz.fido.network.domain.model.swift.GetSwiftCommissionRequest
import uz.fido.network.domain.model.swift.SwiftCommissionResponse
import uz.fido.network.domain.model.swift.SwiftRequest
import uz.fido.network.domain.model.swift.SwiftTransferListRequest
import uz.fido.network.domain.model.swift.SwiftTransferListResponse
import uz.fido.network.domain.model.swift.SwiftTransferResponse

interface PaymentApiInterface {

    @GET
    fun downloadPayments(@Url fileUrl: String?): Call<ResponseBody>

    @GET("GET_PAYMENT_FILE")
    suspend fun getPaymentFile(
        @Header("Authorization") token: String
    ): Payment

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

    @POST("{path}/")
    suspend fun createPayment(
        @Header("Authorization") token: String, @Body createPaymentRequest: CreatePaymentRequest, @Path("path") path: String
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
    fun humoPay(
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
    ): CheckSmsForPaymentResponse

    @POST("GET_REQUEST_IN_PARAMS")
    suspend fun getOperationParams(
        @Header("Authorization") token: String,
        @Body getOperationParamRequest: GetOperationInfoRequest
    ): InParamsResponse

    @POST("GET_BANK_NAME")
    suspend fun getBankName(
        @Header("Authorization") token: String, @Body getBankNameRequest: GetBankNameRequest
    ): BankNameResponse

    @POST("GET_ONE_TIME_PAY_INFO")
    suspend fun getOneTimePayInfo(
        @Header("Authorization") token: String, @Body oneTimeInfoRequest: OneTimeInfoRequest
    ): OneTimeInfoResponse

    @POST("GET_IBS_SWIFT_BICS")
    suspend fun getSwiftBic(
        @Header("Authorization") token: String, @Body bic: SwiftRequest
    ): SwiftTransferResponse

    @POST("GET_IBS_SWIFT_COMISSION")
    suspend fun getSwiftCommission(
        @Header("Authorization") token: String, @Body request: GetSwiftCommissionRequest
    ): SwiftCommissionResponse

    @POST("CREATE_IBS_SWIFT_APP")
    suspend fun createSwiftApp(
        @Header("Authorization") token: String, @Body swiftRequest: CreateSwiftAppRequest
    ): BaseResponse

    @POST("GET_IBS_SWIFT_DOCS")
    suspend fun getSwiftDocs(
        @Header("Authorization") token: String, @Body listRequest: SwiftTransferListRequest
    ): SwiftTransferListResponse


}