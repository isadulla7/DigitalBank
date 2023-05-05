package uz.fido.network.domain.model.cards

import java.io.Serializable

data class OrderCardRequest(
    val command: String,
    val userId: String,
    val design: String,
    val orderType: String,
    var from_object_id: String,
    val cardType: String,
    val amount: String,
    val payPurpose: String,
    val contact: String,
    val smsMobilePhone: String,
    val address: String,
    val mainCardNumber: String,
    var service_id: String,
    val order_filial_code: String,
    var sms_code: String? = "",
    val secretWord: String? = "",
    val city: String? = "",
    val district: String? = "",
    val street: String? = "",
    val house: String? = "",
    val pin_code: String? = ""
) : Serializable