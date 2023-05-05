package uz.fido.network.domain.model.collect_split_money

data class CmUserProducts(
    var product_count: Int,
    val list_menu_id: String,
    val product_name: String,
    var user_product_count: Int,
    val product_sum: String,
    val product_for_all: String? = null
)