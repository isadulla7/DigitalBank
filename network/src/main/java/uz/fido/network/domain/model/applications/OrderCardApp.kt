package uz.fido.network.domain.model.applications

import java.io.Serializable

data class OrderCardApp(
    val state_name: String,
    val create_date: String,
    val product: String,
    var module_product: String,
    val module_product_code: String? = null,
    val state_id: Int,
    val application_id: Int,
    val type: Int? = null,
    val loan_cc_id: String? = ""
) : Serializable