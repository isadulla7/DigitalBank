package uz.fido.network.domain.model.payment

import java.io.Serializable

data class GetPaymentVersionResponse(
    val code: Int,
    val msg: String,
    val version: String
) : Serializable