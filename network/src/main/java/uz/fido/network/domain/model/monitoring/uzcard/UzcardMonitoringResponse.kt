package uz.fido.network.domain.model.monitoring.uzcard

import com.google.gson.annotations.SerializedName

data class UzcardMonitoringResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("transactions") val transactions: ArrayList<UzcardMonitoringItem>,
    @SerializedName("msg") val message: String
)