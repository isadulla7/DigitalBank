package uz.fido.network.domain.model.monitoring.categories

import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem
import java.io.Serializable

data class MonitoringCategoriesResponse(
    val request_id: String,
    val code: Int,
    val merchant_categories: ArrayList<SVMonitoringItem>,
    val msg: String
) : Serializable