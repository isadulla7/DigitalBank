package uz.fido.network.domain.model.news

import java.io.Serializable

data class GetNewsRequest(
    val command: String
) : Serializable