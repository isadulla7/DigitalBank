package uz.fido.network.domain.model.news

import java.io.Serializable

data class News(
    val content: String,
    val id: String,
    var is_read: String,
    val date: String,
    val display: String,
    val img_url: String,
    val title: String
) : Serializable