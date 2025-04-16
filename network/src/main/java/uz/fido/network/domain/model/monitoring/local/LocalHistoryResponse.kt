package uz.fido.network.domain.model.monitoring.local

import java.io.Serializable

data class LocalHistoryResponse(
    val code: Int,
    val msg: String,
    val transacts: ArrayList<LocalHistoryItem>
) : Serializable