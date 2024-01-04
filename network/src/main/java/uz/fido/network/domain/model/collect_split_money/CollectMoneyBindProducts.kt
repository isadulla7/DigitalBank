package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyBindProducts(
    val list_id: String,
    val cm_user_id: String,
    val selected_menu: ArrayList<CmSelectedProducts>
)