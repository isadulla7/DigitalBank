package uz.fido.network.domain.model.monitoring.currency_card

import java.io.Serializable

data class CurrencyCardMonitoringRequest(
    val object_value: String,
    val start_date: String,
    val end_date: String
) : Serializable