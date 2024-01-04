package uz.fido.network.domain.model.payment

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AllServiceLists : Serializable {
    @SerializedName(value = "code", alternate = ["reg_code", "comission_code", "card_code", "id", "value"])
    var code: String? = null

    @SerializedName(value = "price", alternate = ["balance"])
    var price: String? = null

    @SerializedName(value = "name", alternate = ["reg_name", "comission_name", "card_name"])
    var name: String? = null
    var addition: String? = null
    var isSelected: Boolean = true
    var ord: Int? = 0
    var paymentParams: PaymentParams? = null
}