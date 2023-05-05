package uz.fido.network.domain.model.deposits.operations

import java.io.Serializable

data class PartialWithdrawMoneyDepositRequest(
    var command: String,
    var savDepId: String,
    var amount: String,
    var to_object_value: String,
    var service_id: String,
    var to_object_expire: String
) : Serializable