package uz.fido.network.domain.model.monitoring.home

import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import java.io.Serializable

data class ItemHomeHistory(
    var to_object_value: String,
    var module_code: String,
    var amount: String,
    var object_id: String,
    var commission_amount: String? = null,
    var merchant_id: String? = null,
    var create_date: String? = null,
    val id: String? = null,
    val terminal_id: String? = null,
    val service_id: String? = null,
    val request_id: String,
    val state_id: String? = null,
    val currency_code: String? = null,
    val to_object_key: String? = null,
    val signature: String? = null,
    var monitoringInfo: LocalMonitoring? = null
) : Serializable