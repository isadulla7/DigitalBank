package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyManualTransfer(
    val amount: String,
    val purpose: String,
    val list_id: String,
    val list_detail_id: String,
    val cm_user_id: String
)