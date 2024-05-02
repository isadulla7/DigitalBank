package uz.fido.network.domain.model.deposits

data class DepositListResponse(
    val code: Int,
    val deposit_types: ArrayList<Deposit>,
    val msg: String,
    val ora_msg: String
)