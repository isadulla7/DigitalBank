package uz.fido.network.domain.model.payment

import java.io.Serializable

data class GetPaymentVersionRequest(
    val command: String
) : Serializable