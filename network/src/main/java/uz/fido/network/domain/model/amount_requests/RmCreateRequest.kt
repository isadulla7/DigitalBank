package uz.fido.network.domain.model.amount_requests

class RmCreateRequest(
    var requested_sum: String,
    var name: String,
    var object_id: String,
    var receiver_phone_number: String
)