package uz.fido.network.domain.model.deposits.operations

import java.io.Serializable

data class EarlyClosureRequest(
    var command: String,
    var to_object_value: String,
    var to_object_id: String,
    var to_object_expire: String,
    var savDepId: String,
    var credit_amount: String,
    var client_id: String,
    var service_id: String,
    var status: String,
    var closing_date: String
) : Serializable