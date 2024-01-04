package uz.fido.network.domain.model.monitoring.filter

import java.io.Serializable


data class PaymentServiceResponse(
    val code: Int,
    val msg: String,
    val request_id: Int,
    val user_payed_services: ArrayList<UserPayedService>?=null
): Serializable