package uz.fido.network.domain.model.monitoring.uzcard

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UzcardMonitoringRequest(
    @SerializedName("from_object_ids") val selectedCards: ArrayList<String>,
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("end_date") val endDate: String,
    @SerializedName("page_number") val pageNumber: String,
    @SerializedName("page_item_size") val pageItemSize: String
) : Serializable