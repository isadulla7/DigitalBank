package uz.fido.network.domain.model.loans.my_loans

data class CreditListResponse(
    val application_list: ArrayList<Application>,
    val code: Int,
    val credit_list: ArrayList<Credit>,
    val msg: String,
    val offline_credit_list: ArrayList<Credit>,
    val ora_msg: String
)