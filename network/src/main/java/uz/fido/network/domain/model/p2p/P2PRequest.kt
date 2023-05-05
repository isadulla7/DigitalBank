package uz.fido.network.domain.model.p2p

import java.io.Serializable

data class P2PRequest(
    val command: String,
    val amount: String,
    val from_object_value: String,
    val from_object_id: String,
    val service_id: String,
    val to_object_expire: String,
    val to_object_value: String,
    val from_object_expire: String,
    val message: String,
    val get_info: String? = null,
    val phone_number: String? = "",
    var sms_code: String? = null,
    var target: String? = null,
    var target_id: String? = null,
    var string_line: String? = null,
    var i_request_id: String = "",
    val sms_confirm_counter: String? = "Y"
) : Serializable