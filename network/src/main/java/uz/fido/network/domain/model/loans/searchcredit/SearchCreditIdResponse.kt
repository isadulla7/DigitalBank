package uz.fido.network.domain.model.loans.searchcredit

data class SearchCreditIdResponse(
    val code: Int,
    val data: ArrayList<CreditItem>,
    val msg: String,
    val request_id: Int
)