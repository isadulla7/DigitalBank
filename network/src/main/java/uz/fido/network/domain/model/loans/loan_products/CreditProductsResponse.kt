package uz.fido.network.domain.model.loans.loan_products

data class CreditProductsResponse(
    val data: ArrayList<CreditProduct>,
    val request_id: Int,
    val code: Int,
    val msg: String
)