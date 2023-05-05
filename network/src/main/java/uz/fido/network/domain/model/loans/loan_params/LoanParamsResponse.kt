package uz.fido.network.domain.model.loans.loan_params

import java.io.Serializable

data class LoanParamsResponse(
    val code: Int,
    val msg: String,
    val references: ReferencesLoanParams,
    val values: Map<String, String>,
    val keys: Map<String, String>
) : Serializable