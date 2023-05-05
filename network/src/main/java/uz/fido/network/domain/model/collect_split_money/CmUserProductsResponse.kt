package uz.fido.network.domain.model.collect_split_money

data class CmUserProductsResponse(
    val code: Int,
    val msg: String,
    val cm_user_products: ArrayList<CmUserProducts>,
    val person_count: Int,
    val service_percent: Int,
    val list_total_sum: String,
    val product_for_all_sum: String
)