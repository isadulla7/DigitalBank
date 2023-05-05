package uz.fido.network.domain.model.monitoring.uzcard

import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem

data class SvMonitoringOldResponse(
    val tranCount: Int,
    val totalPages: String,
    val page_number: String,
    val last: Boolean,
    val code: Int,
    val numberOfElements: Int,
    val transactions: ArrayList<SVMonitoringItem>,
    val msg: String
)