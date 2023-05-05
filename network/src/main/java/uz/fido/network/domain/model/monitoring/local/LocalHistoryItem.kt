package uz.fido.network.domain.model.monitoring.local

data class LocalHistoryItem(
    var icon_name: String,
    var date_modify: String,
    var module_code: String,
    var amount: String,
    var object_id: Int,
    var commission_amount: String,
    var module_operation_id: Int?,
    var merchant_id: String,
    var create_date: String,
    var short_name: String,
    var id: Int,
    var user_id: Int,
    var terminal_id: String,
    var service_id: Int,
    var request_id: Int,
    var state_id: String,
    var dep_external_id: String,
    var client_id: Int,
    var currency_code: String,
    var signature: String
)