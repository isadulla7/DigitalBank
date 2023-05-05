package uz.fido.network.domain.model.collect_split_money

data class CmCreateResponse(
    val list_id: String,
    val url: String,
    val request_id: String,
    val code: Int,
    val msg: String
)