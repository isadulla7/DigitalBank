package uz.fido.network.domain.model.search

import java.io.Serializable

class SearchDataResponse(
    var paynet_action_type: String? = null,
    var amount: String? = null,
    var params: HashMap<String, String>? = null,
    var operation_code: String? = null,
    var phone_number: String? = null,
    var user_id: String? = null,
    var service_id: String? = null,
    //paynet_hash
    var from_object_expire: String? = null,
    //identification_reponse
    var from_object_value: String? = null,
    var request_id: String? = null,
    var payment_detail_code: String? = null,
    var command: String? = null,
    var code: String? = null,
    var from_object_id: String? = null,
    var filial_code: String? = null,
    var client_id: String? = null,
    var from_object_type: String? = null,
    var curr_level_position: String? = null,
    var msg: String? = null,
    var provider: String? = null,
    var from_object_currency: String? = null,
    var to_object_value: String? = null,
    var language: String? = null,
    var to_object_currency: String? = null,
    var to_object_expire: String? = null,
    var to_embossed_name: String? = null,
    var get_info: String? = null,
    var token: String? = null,
    var to_object_id: String? = null,
    var current_device_code: String? = null,
    var request_code: String? = null,
    var to_object_type: String? = null
) : Serializable
