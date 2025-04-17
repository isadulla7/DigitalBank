package uz.fido.network.data.repository

import retrofit2.Call
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.services.PaymentApiInterface
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
import uz.fido.network.domain.model.payment.Payment
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
import uz.fido.network.domain.model.sms.CheckSmsForPaymentResponse
import uz.fido.network.domain.model.subscriptions.AutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.AutoPaymentResponse
import uz.fido.network.domain.model.subscriptions.ChangeAutoPaymentStateRequest
import uz.fido.network.domain.model.subscriptions.DeleteAutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.network.domain.model.swift.CreateSwiftAppRequest
import uz.fido.network.domain.model.swift.GetSwiftCommissionRequest
import uz.fido.network.domain.model.swift.SwiftCommissionResponse
import uz.fido.network.domain.model.swift.SwiftRequest
import uz.fido.network.domain.model.swift.SwiftTransferListRequest
import uz.fido.network.domain.model.swift.SwiftTransferListResponse
import uz.fido.network.domain.model.swift.SwiftTransferResponse
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(private val paymentService: PaymentApiInterface) :
    IPaymentRepository {

    override suspend fun getPaymentFile(
        token: String
    ): Resource<Payment> = getResult {
        paymentService.getPaymentFile(token)
    }

    override suspend fun preparePayment(
        token: String,
        preparePaymentRequest: PreparePaymentRequest
    ): Resource<PreparePaymentResponse> = getResult {
        paymentService.preparePayment(token, preparePaymentRequest)
    }

    override suspend fun createPayment(
        token: String,
        createPaymentRequest: CreatePaymentRequest
    ): Resource<CreatePaymentResponse> = getResult {
        paymentService.createPayment(token, createPaymentRequest)
    }

    override suspend fun createPayment(
        token: String,
        createPaymentRequest: CreatePaymentRequest,
        path: String
    ): Resource<CreatePaymentResponse> = getResult {
        paymentService.createPayment(token, createPaymentRequest, path)
    }


    override suspend fun loanRepayment(
        token: String,
        createPaymentRequest: CreatePaymentRequest
    ): Resource<CreatePaymentResponse> = getResult {
        paymentService.loanRepayment(token, createPaymentRequest)
    }

    override suspend fun fetchLocalPayments(
        token: String,
        paymentByLocationRequest: PaymentByLocationRequest
    ): Resource<PaymentByLocationResponse> = getResult {
        paymentService.fetchLocalPayments(token, paymentByLocationRequest)
    }


    override suspend fun fetchLocalPaymentTypes(token: String): Resource<LocalPaymentTypesResponse> = getResult {
        paymentService.fetchLocalPaymentTypes(token)
    }

    override suspend fun loadRepayment(
        token: String,
        createPaymentRequest: CreatePaymentRequest
    ): Resource<BaseResponse> = getResult {
        paymentService.loadRepayment(token, createPaymentRequest)
    }

    override suspend fun localPayment(
        token: String,
        localPaymentRequest: LocalPaymentRequest
    ): Resource<BaseResponse> = getResult {
        paymentService.localPayment(token, localPaymentRequest)
    }

    override suspend fun printCheque(
        token: String,
        chequeRequest: PrintChequeRequest
    ): Resource<PrintChequeResponse> = getResult {
        paymentService.printCheque(token, chequeRequest)
    }

    override fun humoPay(
        token: String,
        humoPayRequest: HumoPayRequest
    ): Call<NfcResponse> =
        paymentService.humoPay(token, humoPayRequest)


    override suspend fun getAutoPaymentList(
        token: String,
        autoPaymentRequest: AutoPaymentRequest
    ): Resource<AutoPaymentResponse> = getResult {
        paymentService.getAutoPaymentList(token, autoPaymentRequest)
    }

    override suspend fun createAutoPayment(
        token: String,
        saveAutoPaymentModel: SaveAutoPaymentModel
    ): Resource<BaseResponse> = getResult {
        paymentService.createAutoPayment(token, saveAutoPaymentModel)
    }

    override suspend fun deleteAutoPayment(
        token: String,
        deleteAutoPaymentRequest: DeleteAutoPaymentRequest
    ): Resource<BaseResponse> = getResult {
        paymentService.deleteAutoPayment(token, deleteAutoPaymentRequest)
    }

    override suspend fun changeAutoPaymentState(
        token: String,
        changeAutoPaymentStateRequest: ChangeAutoPaymentStateRequest
    ): Resource<BaseResponse> = getResult {
        paymentService.changeAutoPaymentState(token, changeAutoPaymentStateRequest)
    }


    override suspend fun editAutoPayment(
        token: String,
        saveAutoPaymentModel: SaveAutoPaymentModel
    ): Resource<BaseResponse> = getResult {
        paymentService.editAutoPayment(token, saveAutoPaymentModel)
    }

    override suspend fun checkSmsForPayment(
        token: String,
        checkSmsForPayment: CheckSmsForPayment
    ): Resource<CheckSmsForPaymentResponse> = getResult {
        paymentService.checkSmsForPayment(token, checkSmsForPayment)
    }

    override suspend fun getOperationParams(
        token: String,
        getOperationParamRequest: GetOperationInfoRequest
    ): Resource<InParamsResponse> = getResult {
        paymentService.getOperationParams(token, getOperationParamRequest)
    }


    override suspend fun getBankName(
        token: String,
        getBankNameRequest: GetBankNameRequest
    ): Resource<BankNameResponse> = getResult {
        paymentService.getBankName(token, getBankNameRequest)
    }

    override suspend fun getOneTimePayInfo(
        token: String,
        oneTimeInfoRequest: OneTimeInfoRequest
    ): Resource<OneTimeInfoResponse> = getResult {
        paymentService.getOneTimePayInfo(token, oneTimeInfoRequest)
    }

    override suspend fun getBankNameRequest(
        token: String, getBankNameRequest: GetBankNameRequest
    ): Resource<BankNameResponse> = getResult {
        paymentService.getBankName(token, getBankNameRequest)
    }

    override suspend fun oneTimeInfoRequest(
        token: String, oneTimeInfoRequest: OneTimeInfoRequest
    ): Resource<OneTimeInfoResponse> = getResult {
        paymentService.getOneTimePayInfo(token, oneTimeInfoRequest)
    }

    override suspend fun getSwiftBic(
        token: String, swiftRequest: SwiftRequest
    ): Resource<SwiftTransferResponse> = getResult {
        paymentService.getSwiftBic(token, swiftRequest)
    }

    override suspend fun getSwiftCommission(
        token: String, swiftRequest: GetSwiftCommissionRequest
    ): Resource<SwiftCommissionResponse> = getResult {
        paymentService.getSwiftCommission(token, swiftRequest)
    }

    override suspend fun createSwiftApp(
        token: String, swiftRequest: CreateSwiftAppRequest
    ): Resource<BaseResponse> = getResult {
        paymentService.createSwiftApp(token, swiftRequest)
    }

    override suspend fun getSwiftDocs(
        token: String, swiftRequest: SwiftTransferListRequest
    ): Resource<SwiftTransferListResponse> = getResult {
        paymentService.getSwiftDocs(token, swiftRequest)
    }
}