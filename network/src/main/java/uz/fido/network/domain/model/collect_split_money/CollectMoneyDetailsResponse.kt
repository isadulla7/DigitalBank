package uz.fido.network.domain.model.collect_split_money

import java.io.Serializable

data class CollectMoneyDetailsResponse(
    var cm_list_details: ArrayList<CollectMoneyDetails>,
    var request_id: Int,
    val code: String,
    val msg: String
) : Serializable