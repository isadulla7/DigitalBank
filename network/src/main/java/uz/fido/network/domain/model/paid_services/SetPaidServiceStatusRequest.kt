package uz.fido.network.domain.model.paid_services

import java.io.Serializable

data class SetPaidServiceStatusRequest(
    val amount: String,
    val service_id: String,
    val phone_number: String,
    val sms_code: String,
    val fee_service_id: String,
    val from_object_id: String,
    val state: String,
    val command: String
) : Serializable