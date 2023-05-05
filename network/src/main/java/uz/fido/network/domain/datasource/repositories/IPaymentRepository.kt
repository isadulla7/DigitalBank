package uz.fido.network.domain.datasource.repositories

import retrofit2.Call
import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.InParamsResponse
import uz.fido.network.domain.model.branches.BankNameResponse
import uz.fido.network.domain.model.branches.GetBankNameRequest
import uz.fido.network.domain.model.branches.OneTimeInfoRequest
import uz.fido.network.domain.model.branches.OneTimeInfoResponse
import uz.fido.network.domain.model.humo_pay.HumoPayRequest
import uz.fido.network.domain.model.humo_pay.NfcResponse
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.CreatePaymentResponse
import uz.fido.network.domain.model.payment.GetPaymentVersionRequest
import uz.fido.network.domain.model.payment.GetPaymentVersionResponse
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentResponse
import uz.fido.network.domain.model.payment.PrintChequeRequest
import uz.fido.network.domain.model.payment.PrintChequeResponse
import uz.fido.network.domain.model.payment.location.LocalPaymentRequest
import uz.fido.network.domain.model.payment.location.LocalPaymentTypesResponse
import uz.fido.network.domain.model.payment.location.PaymentByLocationRequest
import uz.fido.network.domain.model.payment.location.PaymentByLocationResponse
import uz.fido.network.domain.model.search.GetOperationInfoRequest
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.network.domain.model.subscriptions.AutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.AutoPaymentResponse
import uz.fido.network.domain.model.subscriptions.ChangeAutoPaymentStateRequest
import uz.fido.network.domain.model.subscriptions.DeleteAutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel

interface IPaymentRepository {

    suspend fun preparePayment(
        token: String, preparePaymentRequest: PreparePaymentRequest
    ): Resource<PreparePaymentResponse>

    suspend fun createPayment(
        token: String, createPaymentRequest: CreatePaymentRequest
    ): Resource<CreatePaymentResponse>

    suspend fun loanRepayment(
        token: String, createPaymentRequest: CreatePaymentRequest
    ): Resource<CreatePaymentResponse>

    suspend fun fetchLocalPayments(
        token: String, paymentByLocationRequest: PaymentByLocationRequest
    ): Resource<PaymentByLocationResponse>

    suspend fun fetchLocalPaymentTypes(
        token: String
    ): Resource<LocalPaymentTypesResponse>

    suspend fun getPaymentVersion(
        token: String, getPaymentVersionRequest: GetPaymentVersionRequest
    ): Resource<GetPaymentVersionResponse>

    suspend fun loadRepayment(
        token: String, createPaymentRequest: CreatePaymentRequest
    ): Resource<BaseResponse>

    suspend fun localPayment(
        token: String, localPaymentRequest: LocalPaymentRequest
    ): Resource<BaseResponse>

    suspend fun printCheque(
        token: String, chequeRequest: PrintChequeRequest
    ): Resource<PrintChequeResponse>

    suspend fun humoPay(
        token: String, humoPayRequest: HumoPayRequest
    ): Resource<Call<NfcResponse>>

    suspend fun getAutoPaymentList(
        token: String, autoPaymentRequest: AutoPaymentRequest
    ): Resource<AutoPaymentResponse>

    suspend fun createAutoPayment(
        token: String, saveAutoPaymentModel: SaveAutoPaymentModel
    ): Resource<BaseResponse>

    suspend fun deleteAutoPayment(
        token: String, deleteAutoPaymentRequest: DeleteAutoPaymentRequest
    ): Resource<BaseResponse>

    suspend fun changeAutoPaymentState(
        token: String, changeAutoPaymentStateRequest: ChangeAutoPaymentStateRequest
    ): Resource<BaseResponse>

    suspend fun editAutoPayment(
        token: String, saveAutoPaymentModel: SaveAutoPaymentModel
    ): Resource<BaseResponse>

    suspend fun checkSmsForPayment(
        token: String, checkSmsForPayment: CheckSmsForPayment
    ): Resource<BaseResponse>

    suspend fun getOperationParams(
        token: String, getOperationParamRequest: GetOperationInfoRequest
    ): Resource<InParamsResponse>

    suspend fun getBankName(
        token: String, getBankNameRequest: GetBankNameRequest
    ): Resource<BankNameResponse>

    suspend fun getOneTimePayInfo(
        token: String, oneTimeInfoRequest: OneTimeInfoRequest
    ): Resource<OneTimeInfoResponse>

}