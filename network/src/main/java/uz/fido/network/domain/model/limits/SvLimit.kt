package uz.fido.network.domain.model.limits

import java.io.Serializable

data class SvLimit(
    val limitId: String,
    val cycleType: String,
    val usedAmount: String,
    val dsc: String,
    val limitAmount: String,
    val description: String
) : Serializable