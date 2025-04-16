package uz.fido.network.domain.model.conversion

import java.io.Serializable

data class ConversionRequest(
    val command: String,
    val from_object_value: String? = null,
    val from_object_id: String,
    val to_object_value: String,
    val to_object_expire: String,
    val amount: String,
    val currency_code: String,
    val service_id: String,
    var sms_code: String? = null
) : Serializable