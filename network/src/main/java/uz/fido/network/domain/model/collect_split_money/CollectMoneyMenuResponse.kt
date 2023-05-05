package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyMenuResponse(
    val request_id: String,
    val code: Int,
    val msg: String,
    val cm_list_menu: ArrayList<CollectMoneyMenu>
)