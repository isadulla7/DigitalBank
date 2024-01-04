package uz.fido.network.domain.model.paid_services

import java.io.Serializable

data class PaidService(
    val amount: String,
    val create_date: String,
    val duration: String,
    val service_id: String,
    val name: String,
    var user_service_status: ServiceStatus
) : Serializable