package uz.fido.network.domain.model.collect_split_money

data class CollectMoneyTransferRequest(
    val list_id: String,
    val from_object_id: String,
    val from_object_expire: String,
    val amount: String,
    val purpose: String,
    val to_object_expire: String,
    val to_object_value: String,
    val command: String = "card&card",
    val service_id: String = "-1",
    var sms_code: String? = null,
)