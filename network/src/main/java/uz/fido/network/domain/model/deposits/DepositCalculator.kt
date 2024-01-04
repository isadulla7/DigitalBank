package uz.fido.network.domain.model.deposits

import java.io.Serializable

data class DepositCalculator(
    val amount: String,
    val receiveSum: String,
    val date: String,
    val saldo: String,
    val proc: String,
    var count:Int=1
): Serializable