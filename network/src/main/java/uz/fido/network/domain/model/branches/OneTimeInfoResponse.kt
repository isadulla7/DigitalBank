package uz.fido.network.domain.model.branches

import java.io.Serializable

class OneTimeInfoResponse(
    var fee_percent: String = "",
    var receiver_account_code: String = "",
    var fee_amount: String = "",
    var receiver_filial_code: String = "",
    var payment_purpose: String = "",
    var is_our_bank: String = "",
    var request_id: String = "",
    var code: Int,
    var budget_account_code: String,
    var msg: String,
    var inn: String = "",
    var client_name: String = ""
) : Serializable