package uz.fido.network.domain.model.monitoring.local

import java.io.Serializable

data class GetLocalHistoryRequest(
    val command: String,
    val object_id: String
) : Serializable