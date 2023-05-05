package uz.fido.network.domain.model.loans.loan_graph


data class CreditGraphResponse(
    val code: Int,
    val data: ArrayList<CreditGraph>,
    val msg: String
)