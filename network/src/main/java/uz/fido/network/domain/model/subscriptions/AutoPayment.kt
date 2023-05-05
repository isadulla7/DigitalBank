package uz.fido.network.domain.model.subscriptions

import java.io.Serializable

data class AutoPayment(
    val amount: String,
    val days: ArrayList<Int>,
    val device_code: String,
    val device_name: String,
    val device_type: String,
    val hour: Int,
    val icon_name: String,
    val id: Int,
    val last_executed_date: String,
    val modified_by: Int,
    val modified_on: String,
    val months: ArrayList<Int>,
    val name: String,
    val need_confirm: String,
    val payment_service_id: String,
    val provider_type: Int,
    var state: String,
    val type: String,
    val user_id: Int,
    val account: String,
    val account_text: String,
    val selected_days: ArrayList<String>? = null
) : Serializable