package uz.fido.network.domain.model.paid_services

import java.io.Serializable

data class ServiceStatus(
    var state: String,
    val start_date: String,
    val is_paid: String,
    val end_date: String,
    val user_service_id: String
) : Serializable