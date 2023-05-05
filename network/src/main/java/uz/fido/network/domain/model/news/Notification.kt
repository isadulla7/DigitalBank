package uz.fido.network.domain.model.news

import java.io.Serializable

data class Notification(
    val text: String,
    val user_id: String,
    val notification_id: String,
    val is_general: String,
    var is_read: String,
    val group_code: String,
    val oper_code: String,
    val service_id: String,
    val request_code: String,
    val created_on: String,
    val title: String,
    val image: String
) : Serializable