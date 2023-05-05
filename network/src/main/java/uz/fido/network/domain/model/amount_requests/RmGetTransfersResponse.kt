package uz.fido.network.domain.model.amount_requests

import java.io.Serializable

class RmGetTransfersResponse(
    var transfer_list: ArrayList<RmTransferItem>,
    var request_id: Int,
    val code: String,
    val msg: String
) : Serializable