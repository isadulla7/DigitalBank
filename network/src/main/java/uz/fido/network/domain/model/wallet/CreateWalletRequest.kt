package uz.fido.network.domain.model.wallet

data class CreateWalletRequest(
    val filial_code: String,
    val code_currency: String,
    val name: String
)