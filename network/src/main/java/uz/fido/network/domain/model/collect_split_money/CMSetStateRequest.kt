package uz.fido.network.domain.model.collect_split_money

data class CMSetStateRequest(
    val list_id: String,
    val state: String? = null
)