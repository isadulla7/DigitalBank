package uz.fido.network.domain.model.invoice

import java.io.Serializable

data class InvoiceListResponse(
    val request_id: String,
    val user_e_invoice_reqs: ArrayList<ItemInvoice>,
    val code: String,
    val msg: String
) : Serializable