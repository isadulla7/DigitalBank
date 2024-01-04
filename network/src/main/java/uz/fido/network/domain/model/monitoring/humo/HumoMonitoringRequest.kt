package uz.fido.network.domain.model.monitoring.humo

import java.io.Serializable

data class HumoMonitoringRequest(
    val from_object_id: String,
    val start_date: String,
    val end_date: String
) : Serializable