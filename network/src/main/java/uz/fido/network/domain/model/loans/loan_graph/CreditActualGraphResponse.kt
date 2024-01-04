package uz.fido.network.domain.model.loans.loan_graph


data class CreditActualGraphResponse(
    val code: Int,
    val data: ArrayList<CreditActualGraph>,
    var nextPayment: String,
    var nextPaymentDate: String,
    val msg: String
)