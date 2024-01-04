package uz.fido.network.domain.model.invoice

data class InvoicePaymentRequest(
    val e_invoice_req_id: String,
    val from_object_id: String,
    val amount: String,
    val pay_onspot_id: String,
    val command: String,
    val service_id: Int = -19
)