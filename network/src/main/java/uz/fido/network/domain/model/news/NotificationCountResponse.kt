package uz.fido.network.domain.model.news

data class NotificationCountResponse(
    val unreaded_notif_count: Int,
    val request_id: String,
    val code: Int,
    val unreaded_news_count: Int,
)