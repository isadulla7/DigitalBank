package uz.fido.network.domain.model.p2p

data class P2PInfoRequest(
    val command: String,
    val expire: String,
    val service_id: String,
    val to_object_value: String,
    val is_by_phone: String? = "N",
    val from_object_id: String
)