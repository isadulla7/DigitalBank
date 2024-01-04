package uz.fido.network.domain.model.monitoring.local

import uz.fido.network.domain.model.monitoring.local.LocalHistoryItem
import java.io.Serializable

data class LocalHistoryResponse(
    val code: Int,
    val msg: String,
    val transacts: ArrayList<LocalHistoryItem>
) : Serializable