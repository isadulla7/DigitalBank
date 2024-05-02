package uz.fido.network.domain.model.monitoring.filter


import java.io.Serializable

data class MonitoringFilterCardResponse(
    val code: Int,
    val msg: String,
    val request_id: Int,
    var user_objects: ArrayList<FilterCard>?=null
): Serializable