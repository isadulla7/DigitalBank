package uz.fido.network.domain.model.popular_transfers

import java.io.Serializable

data class PopularTransfers(
    var user_avatar: String = "",
    var card_number: String? = null,
    var is_favourite: String? = null,
    var user_id: String? = null,
    var obj_exp_date: String? = null,
    var empbossed_name: String? = null,
    var object_type: String? = null,
    var object_value: String? = null,
    var tansfer_count: String? = null,
    var created_date: String? = null,
    var client_id: String? = null,
    var phone: String? = null
) : Serializable