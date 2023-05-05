package uz.fido.network.domain.model.collect_split_money

import java.io.Serializable

data class CollectMoneyListResponse(
    val request_id: Int,
    val code: String,
    var cm_list: ArrayList<CollectMoneyList>,
    val msg: String
) : Serializable