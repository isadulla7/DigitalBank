package uz.fido.network.domain.model.collect_split_money

data class CmAddUsersRequest(
    val list_id: String,
    val phone_numbers: ArrayList<String>
)