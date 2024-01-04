package uz.fido.network.domain.model.collect_split_money


data class CollectMoneyCreateRequest(
    val cm_type: String = "O",
    val person_count: Int? = null,
    val total_sum: String? = null,
    val name: String,
    val object_id: String,

    val menu: ArrayList<CollectMoneyMenu>? = null,
    val collected_sum: Int? = null,
    val collect_money_url: String? = null,
    val product_name: String? = null,
    val cm_user_id: String? = null,
    val service_percent: Int? = null,
    val product_count: String? = null,
    val product_sume: String? = null,
    val product_total_sum: String? = null,
    val phone_numbers: ArrayList<String>? = null
)