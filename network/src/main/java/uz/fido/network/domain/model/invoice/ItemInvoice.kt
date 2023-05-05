package uz.fido.network.domain.model.invoice

import java.io.Serializable

data class ItemInvoice(
    val merchant_name: String,
    val amount: String,
    val id: String,
    val purpose: String,
    val pay_onspot_id: String,
    val address: String,
    val state: String,
    val created_on: String
) : Serializable