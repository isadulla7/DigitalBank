package uz.fido.network.domain.model.inn

data class DebtInfoResponse(
    val debt_list: ArrayList<InnDebtInfo>? = null,
    val code: Int,
    val msg: String
)