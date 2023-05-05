package uz.fido.network.domain.model.p2p

data class P2PInfoResponse(
    val code: Int,
    val empbossed_name: String? = null,
    val msg: String,
    val ora_msg: String,
    val percent: String,
    val to_object_expire: String,
    val to_object_value: String,
    val to_object_type: String,
    val max_amount: String,
    val min_amount: String,
    val request_id: String
)