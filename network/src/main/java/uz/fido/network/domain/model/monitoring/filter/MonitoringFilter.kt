package uz.fido.network.domain.model.monitoring.filter

import java.io.Serializable

class MonitoringFilter(
    val name:String,
    val type:String,
    val current:Boolean
):Serializable