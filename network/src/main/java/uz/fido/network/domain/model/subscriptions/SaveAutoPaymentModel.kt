package uz.fido.network.domain.model.subscriptions

import java.io.Serializable

data class SaveAutoPaymentModel(
    val payment_service_id: String? = null,
    var selected_days: ArrayList<String>? = null,
    var from_object_id: String? = null,
    var object_value: String? = null,
    var name: String? = null,
    val need_confirm: String = "N",
    var phone_number: String? = null,
    val device_code: String? = null,
    val device_name: String? = null,
    val device_type: String? = null,
    val payment_details: HashMap<String, String>? = null,
    var amount: String? = null,
    var check_status: String? = "РАЗРЕШЕНО",
    var months: ArrayList<Int>? = null,
    var days: ArrayList<Int>? = null,
    var monthsName: String? = null,
    var daysName: String? = null,
    var hours: String? = null,
    val payment_type: String? = null,
    var type: String? = null,
    var auto_payment_id: String? = null
) : Serializable