package uz.fido.network.domain.model.monitoring.uzcard

import java.io.Serializable

data class SVMonitoringRequest(
    val card_numbers: ArrayList<String>,
    val start_date: String = "",
    val end_date: String,
    val page_number: String,
    val page_item_size: String,
    val is_credit: Int? = null
) : Serializable