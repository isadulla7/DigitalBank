package uz.fido.network.domain.model.amount_requests

import java.io.Serializable

class RmList(
    var list_name: String,
    var card_number: String,
    var create_date: String,
    var requested_sum: String,
    var collected_sum: String,
    var user_fio: String,
    var list_id: Int,
    var url: String,
    var state: String
) : Serializable