package uz.fido.network.domain.model.payment

import java.io.Serializable

data class PrintChequeRequest(
    val cheque_request_id: String
) : Serializable