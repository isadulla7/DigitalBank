package uz.fido.network.domain.model.wallet

import java.io.Serializable

data class DeleteWalletRequest(
    val token:String,
    val object_id: String
) : Serializable