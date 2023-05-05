package uz.fido.network.domain.model.loans.loan_groups

data class CreditListResponse(
    val code: Int,
    val credit_products: ArrayList<CreditProduct>,
    val msg: String,
    val request_id: Int
)