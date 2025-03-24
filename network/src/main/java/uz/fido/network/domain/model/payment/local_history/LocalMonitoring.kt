package uz.fido.network.domain.model.payment.local_history

import com.google.gson.annotations.SerializedName
import uz.fido.network.domain.model.payment.Cheque
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.search.SearchDataResponse
import java.io.Serializable
import java.math.BigDecimal

data class LocalMonitoring(
    @SerializedName("icon_name") var iconName: String = "",
    @SerializedName("amount") var amount: String = "",
    @SerializedName("tran_type") var transactionType: String = "",
    @SerializedName("fee_amount") var feeAmount: String = "",
    @SerializedName("fee_percent") var feePercent: String = "",
    @SerializedName("service_id") var serviceId: String = "",
    @SerializedName("terminal_id") var terminalId: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("request_id") var requestId: String = "",
    @SerializedName("state_id") var stateId: String? = "",
    @SerializedName("created_date") var createdDate: String = "",
    @SerializedName("to_obj_name") var receiverCardName: String = "",
    @SerializedName("object_value") var senderCard: String = "",
    @SerializedName("partner_obj") var partnerObj: String = "",
    @SerializedName("currency_code") var currencyCode: String = "",
    @SerializedName("to_embossed_name") var toEmbossedName: String? = "",
    @SerializedName("details") var details: ArrayList<Cheque>? = null,
    @SerializedName("isChecked") var isChecked: Boolean = false,
    @SerializedName("searchData") var searchData: SearchDataResponse? = null
) : Serializable

data class ChartData(
    val paymentGroup: PaymentGroup? = null,
    val paymentService: PaymentService? = null,
    val serviceId: String,
    val amount: BigDecimal
)