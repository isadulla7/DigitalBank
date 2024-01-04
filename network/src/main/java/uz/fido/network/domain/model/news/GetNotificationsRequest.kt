package uz.fido.network.domain.model.news

import java.io.Serializable

data class GetNotificationsRequest(
    val page_number: String,
    val page_item_size: String
) : Serializable