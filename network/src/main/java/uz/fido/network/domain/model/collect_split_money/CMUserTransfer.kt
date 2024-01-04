package uz.fido.network.domain.model.collect_split_money

data class CMUserTransfer(
    val card_number: String,
    val purpose: String?= "",
    val created_date: String,
    val transfer_type: String,
    val transfer_sum: String,
    var user_name: String
)