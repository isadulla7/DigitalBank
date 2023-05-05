package uz.fido.network.domain.model.applications

import java.io.Serializable

data class GetProductDetailsRequest(
    val application_id: String
) : Serializable