package uz.fido.network.domain.model.loans.overdraft

data class OverdraftGraphResponse(
    val data: ArrayList<OverdraftDetail2>,
    val request_id: String,
    val code: Int,
    val msg: String
)