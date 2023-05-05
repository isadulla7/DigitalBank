package uz.fido.network.domain.model.deposits

import uz.fido.network.domain.model.deposits.Deposit

data class DepositListResponse(
    val code: Int,
    val deposit_types: ArrayList<Deposit>,
    val msg: String,
    val ora_msg: String
)