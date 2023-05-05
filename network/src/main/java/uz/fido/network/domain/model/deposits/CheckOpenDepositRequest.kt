package uz.fido.network.domain.model.deposits

import java.io.Serializable

data class CheckOpenDepositRequest(
    val depId: String,
    val amount: String,
    val command: String
) : Serializable