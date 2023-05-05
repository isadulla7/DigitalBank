package uz.fido.network.domain.model.loans

import java.io.Serializable

data class GetLoanRequest(
    val command: String,
    val amount: String,
    val to_object_value: String,
    val loanId: String,
    val service_id: String,
    val sms_code: String,
    val phone_number: String
) : Serializable