package uz.fido.network.domain.model.loans.calculate_loan

import java.io.Serializable

data class CalcLoanRequest(
    val token: String,
    val command: String,
    val product_id: String,
    val amount: String
) : Serializable