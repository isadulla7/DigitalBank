package uz.fido.network.domain.model.deposits.operations

import java.io.Serializable

data class InvestMoneyToDepositRequest(
    var command: String,
    var savDepId: String,
    var amount: String,
    var from_object_id: String,
    var service_id: String,
    var string_line: String? = ""
) : Serializable