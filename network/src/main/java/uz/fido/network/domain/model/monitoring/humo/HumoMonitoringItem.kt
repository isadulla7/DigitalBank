package uz.fido.network.domain.model.monitoring.humo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HumoMonitoringItem(
    @SerializedName("merchant_name") var merchantName: String,
    @SerializedName("merchant_id") var merchantId: String,
    @SerializedName("ref_number") var refNumber: String,
    @SerializedName("tran_type") var transactionType: String,
    @SerializedName("terminal_id") var terminalId: String,
    @SerializedName("tran_date") var transactionDate: String,
    @SerializedName("bank_code") var bankCode: String,
    @SerializedName("card_num") var cardNumber: String,
    @SerializedName("tran_amount") var transactionAmount: String,
    @SerializedName("address") var address: String,
    @SerializedName("category_name") var categoryName: String
) : Serializable