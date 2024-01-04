package uz.fido.network.domain.model.loans.my_loans

import java.io.Serializable

data class CreditListRequest(
    val token: String,
    val command: String,
    val user_id: String
) : Serializable