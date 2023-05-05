package uz.fido.network.domain.model.chat

import java.io.Serializable

data class MessageHistory(
    var created_date: String? = null,
    val msg_id: String? = null,
    var msg_object_id: String? = null,
    var msg_text: String? = null,
    val msg_type_id: Int? = null,
    var ref_msg_id: String? = null,
    var ref_user_id: String? = null,
    var ref_user_name: String? = null,
    var state_id: String? = "",
    var content_type: String? = null,
    var room_id: String? = null,
    var file_size: String? = null,
    var is_mine: Boolean = false,
    val created_user_id: String,
    val owner_user_id: String = "",
    val owner_user_name: String = "",
    val ref_msg_text: String? = null,
    var is_forwarded: String? = "N",
    var is_header: Boolean = false,
    var animate: Boolean = false
) : Serializable