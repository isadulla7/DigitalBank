package uz.fido.network.domain.model.loans.loan_graph

import java.io.Serializable

data class CreditGraphRequest(
    val loanId: String
) : Serializable