package uz.fido.network.domain.model.deposits.operations

import java.io.Serializable

data class EarlyClosureResponse(
    var code: Int,
    var msg: String,
    var operationsId: String,
    var receiveSum: String,
    var returnSum: String,
    var sumDep: String
) : Serializable