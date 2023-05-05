package uz.fido.network.domain.model.amount_requests

import java.io.Serializable

data class RmTransferItem(
    var card_number: String,
    var create_date: String,
    var transfer_amount: String,
    var fio: String
) : Serializable