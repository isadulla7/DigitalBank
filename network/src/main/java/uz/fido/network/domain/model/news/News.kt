package uz.fido.network.domain.model.news

import java.io.Serializable

data class News(
    val content: String,
    val date: String,
    val is_display: String,
    val img_url: String,
    val title: String,
    var is_read: String,
    val id: String
) : Serializable