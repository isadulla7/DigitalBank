package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyAddUserRequest(
    val list_detail_id: String,
    val cm_user_id: String? = null,
    val cm_phone_number: String? = null
)