package uz.fido.network.domain.model.payment.local_history

data class LocalMonitoringRequest(
    val card_number: String? = null,
    val page_item_size: String,
    val page_number: String,
    val start_date: String,
    val end_date: String,
    val service_id: String? = null,
    val object_ids: ArrayList<String>? = null,
    val terminal_id: String? = null
)