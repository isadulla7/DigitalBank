package uz.fido.network.domain.model.monitoring.home

import java.io.Serializable

data class HomeHistoryRequest(
    val page_number: String? = null,
    val page_item_size: String = "20",
    val end_date: String? = null,
    val receiver_acc: String? = null,
    val start_date: String? = null
) : Serializable