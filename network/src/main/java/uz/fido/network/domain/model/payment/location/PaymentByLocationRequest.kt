package uz.fido.network.domain.model.payment.location

import com.google.gson.annotations.SerializedName

data class PaymentByLocationRequest(
    @SerializedName("x_cordinate")
    val x_coordinate: String,
    @SerializedName("y_cordinate")
    val y_coordinate: String,
    val radius: String,
    val page_item_size: String? = null,
    val page_number: String? = null,
    val type_id: String? = null,
    val text: String? = null,
    val last_date: String? = null
)