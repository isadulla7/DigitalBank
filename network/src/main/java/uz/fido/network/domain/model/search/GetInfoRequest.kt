package uz.fido.network.domain.model.search

import java.io.Serializable

class GetInfoRequest(
    val search_request_id: String
) : Serializable

class GetOperationInfoRequest(
    val request_id: String
) : Serializable
