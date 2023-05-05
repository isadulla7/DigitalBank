package uz.fido.network.domain.model.loans.loan_available_amount

import java.io.Serializable

data class GetAmountRequest(
    val command: String,
    val token: String,
    val product_id: String,
    val object_id: String
) : Serializable