package uz.fido.network.domain.model.monitoring.humo

import java.io.Serializable

data class HumoMonitoringRequest(
    val card_number: String,
    val start_date: String,
    val end_date: String
) : Serializable