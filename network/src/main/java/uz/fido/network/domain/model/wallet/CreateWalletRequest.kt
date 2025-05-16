package uz.fido.network.domain.model.wallet

import com.google.gson.annotations.SerializedName

data class CreateWalletRequest(
    @SerializedName("filial_code")
    val filialCode: String,
    @SerializedName("code_currency")
    val codeCurrency: String,
    @SerializedName("name")
    val walletName: String
)