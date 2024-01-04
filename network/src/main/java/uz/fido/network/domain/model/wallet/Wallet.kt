package uz.fido.network.domain.model.wallet

import java.io.Serializable

data class Wallet(
    val name: String,
    val position: Int,
) : Serializable