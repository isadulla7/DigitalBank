package uz.fido.network.domain.model.monitoring.humo

import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem

data class HumoMonitoringResponse(
    val tranCount: String,
    val totalPages: String,
    val page_number: String,
    val last: String,
    val request_id: String,
    val code: Int,
    val numberOfElements: Int,
    val transactions: ArrayList<HumoMonitoringItem>,
    val msg: String
)