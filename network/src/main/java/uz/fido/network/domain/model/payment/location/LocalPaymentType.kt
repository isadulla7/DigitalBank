package uz.fido.network.domain.model.payment.location

import java.io.Serializable

data class LocalPaymentType(
    val code: String? = null,
    val icon_name: String? = null,
    val id: String ?= null,
    val name: String ?= null,
    val order_number: String? = null,
    val state: String ?= null
): Serializable