package uz.fido.network.domain.model.deposits

import java.io.Serializable

data class DepositCalculatorResponse(
    val data: ArrayList<DepositCalculator>,
    val code: Int,
    val msg: String
): Serializable