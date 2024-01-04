package uz.fido.network.domain.model.monitoring.local

import java.io.Serializable

data class NewMonitoringFilterRequest(
    val start_date: String,
    val end_date: String,
    val page_number: String,
    val page_item_size: String,
    val service_ids: ArrayList<String>,
    val oper_type: String,
    val object_ids: ArrayList<String>,
    val object_values: ArrayList<String>,
    val max_amount: String,
    val min_amount: String,
    val terminal_id: String = ""
) : Serializable