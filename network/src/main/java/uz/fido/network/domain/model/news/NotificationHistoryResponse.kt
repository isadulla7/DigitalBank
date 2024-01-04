package uz.fido.network.domain.model.news

data class NotificationHistoryResponse(
    val totalPages: Int,
    val last: String,
    val request_id: String,
    val code: Int,
    val msg: String,
    val notifications: ArrayList<Notification>
)