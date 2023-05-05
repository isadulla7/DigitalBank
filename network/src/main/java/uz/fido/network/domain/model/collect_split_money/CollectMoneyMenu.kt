package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyMenu(
    var name: String,
    val product_count: String,
    val product_sum: String,
    val product_name: String? = null,
    val product_total_sum: String,
    val total_sum: String,
    val id: String? = null,
    val product_for_all: String? = null
)