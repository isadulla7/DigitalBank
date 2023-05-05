package uz.fido.network.domain.model.collect_split_money

import java.io.Serializable

data class CollectMoneyDetails(
    var card_number: String = "",
    var created_date: String= "",
    var transfer_sum: String? = "0",
    var fio: String = "",
    val transfer_count: Int? = null,
    val user_id: String? = "-1",
    val user_name: String? = null,
    var dept_sum: String? = "0",
    val can_change_user: String? = null,
    val id: String = "",
    var order: Int = 999,
    var detail_url: String? = null
) : Serializable