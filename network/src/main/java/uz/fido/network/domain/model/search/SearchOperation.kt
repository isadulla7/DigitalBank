package uz.fido.network.domain.model.search

import java.io.Serializable

class SearchOperation(
    val icon_name: String,
    val info_text: String,
    val request_id: String
) : Serializable
