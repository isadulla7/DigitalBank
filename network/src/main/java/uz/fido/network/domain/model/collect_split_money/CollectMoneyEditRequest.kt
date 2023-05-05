package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyEditRequest(
    val list_id: String,
    val menu: ArrayList<CollectMoneyMenu>
)