package uz.fido.network.domain.model.search

import java.io.Serializable

class SearchResponse(
    val operations: ArrayList<SearchOperation>,
    val request_id: String,
    val code: String,
    val msg: String
) : Serializable
