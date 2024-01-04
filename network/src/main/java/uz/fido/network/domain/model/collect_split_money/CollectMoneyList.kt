package uz.fido.network.domain.model.collect_split_money

import java.io.Serializable

data class CollectMoneyList(
    val person_count: Int? = 0,
    val list_name: String? = "",
    val card_number: String = "",
    var create_date: String = "",
    var list_id: String,
    var collected_sum: String? = "0",
    var total_sum: String? = "",
    var service_percent: Int? = 0,
    var url: String? = "",
    var state: String? = "",
    val cm_type: String? = "",
    val calc_total_sum: String? = ""
) : Serializable