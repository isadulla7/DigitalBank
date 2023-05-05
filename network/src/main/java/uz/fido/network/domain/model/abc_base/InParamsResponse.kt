package uz.fido.network.domain.model.abc_base

import java.io.Serializable

class InParamsResponse(
    var params: HashMap<String, String>? = null,
    var to_object_value: String? = null,
    var amount: String? = null,
    val phone_number: String? = null,
    val to_object_expire: String? = null,
    val service_id: String? = null,
    val from_object_expire: String? = null,
    val from_object_value: String? = null,
    val command: String? = null,
    val from_object_id: String? = null,
    val filial_code: String? = null,
    val client_id: String? = null,
) : Serializable