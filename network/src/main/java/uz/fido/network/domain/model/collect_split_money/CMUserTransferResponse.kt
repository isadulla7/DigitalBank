package uz.fido.network.domain.model.collect_split_money

data class CMUserTransferResponse(
    val request_id: String,
    val code: Int,
    val msg: String,
    val cm_user_transfer: ArrayList<CMUserTransfer>
)