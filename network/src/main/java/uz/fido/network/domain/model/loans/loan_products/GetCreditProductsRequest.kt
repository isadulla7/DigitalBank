package uz.fido.network.domain.model.loans.loan_products

import java.io.Serializable

data class GetCreditProductsRequest(
    val token: String,
    val command: String,
    val credit_group_id: String
) : Serializable