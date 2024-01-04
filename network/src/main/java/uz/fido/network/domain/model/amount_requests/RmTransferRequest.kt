package uz.fido.network.domain.model.amount_requests

class RmTransferRequest(
    var list_id: String,
    var purpose: String,
    val command: String,
    val amount: String,
    val from_object_id: String,
    val service_id: String,
    val to_object_expire: String,
    val to_object_value: String,
    val from_object_expire: String,
    val message: String,
    val sms_code: String
)