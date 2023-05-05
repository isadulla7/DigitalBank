package uz.fido.network.domain.model.deposits.operations

import java.io.Serializable

data class PartialWithdrawMoneyDepositResponse(
    var code: Int,
    var msg: String,
    var operationsId: String
) : Serializable