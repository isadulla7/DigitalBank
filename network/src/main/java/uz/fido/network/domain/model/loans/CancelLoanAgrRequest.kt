package uz.fido.network.domain.model.loans

data class CancelLoanAgrRequest(
    val loanId: String,
    val reasonDesc: String
)