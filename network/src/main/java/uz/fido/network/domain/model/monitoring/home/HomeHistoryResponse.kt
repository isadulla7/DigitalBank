package uz.fido.network.domain.model.monitoring.home

data class HomeHistoryResponse(
    val totalPages: String,
    val last: Boolean,
    val request_id: Int,
    val code: String,
    val transactions: ArrayList<ItemHomeHistory>
)