package uz.fido.network.domain.model.monitoring.uzcard

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UzcardMonitoringItem(
    @SerializedName("merchant_name") val merchantName: String,
    @SerializedName("reversal") val cancelled: String,
    @SerializedName("merchant_id") val merchantId: String,
    @SerializedName("tran_type") val transactionType: String,
    @SerializedName("terminal_id") val terminalId: String,
    @SerializedName("tran_date") val transactionDate: String,
    @SerializedName("card_num") val cardNumber: String,
    @SerializedName("tran_amount") val transactionAmount: String,
    @SerializedName("address") val address: String
) : Serializable