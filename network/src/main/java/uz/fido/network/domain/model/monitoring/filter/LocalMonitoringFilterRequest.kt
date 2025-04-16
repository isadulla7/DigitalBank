package uz.fido.network.domain.model.monitoring.filter

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocalMonitoringFilterRequest(
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("page_number") val pageNumber: Int,
    @SerializedName("page_item_size") val pageItemSize: Int,
    @SerializedName("service_ids") val serviceIds: ArrayList<Int>? = null,
    @SerializedName("object_ids") val objectIds: ArrayList<Int>? = null,
    @SerializedName("to_object_value") val toObjectValue: ArrayList<String>? = null,
    @SerializedName("max_amount") val maxAmount: String? = null,
    @SerializedName("min_amount") val minAmount: String? = null
) : Serializable