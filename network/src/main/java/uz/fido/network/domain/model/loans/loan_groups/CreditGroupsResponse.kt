package uz.fido.network.domain.model.loans.loan_groups

data class CreditGroupsResponse(
    val code: Int,
    val credit_products: ArrayList<CreditGroup>,
    val request_id:String,
    val msg: String,
    val ora_msg: String=""
)