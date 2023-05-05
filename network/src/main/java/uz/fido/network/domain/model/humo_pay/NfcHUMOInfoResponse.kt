package uz.fido.network.domain.model.humo_pay

data class NfcHUMOInfoResponse(
    val msg: String,
    val bank_bin_code: String,
    val card_holder_id: String,
    val masked_pan: String? = null,
    val code: Int
)