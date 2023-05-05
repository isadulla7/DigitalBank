package uz.fido.network.domain.model.money_transfer.receive

import java.io.Serializable

data class Country(
    val name: String,
    val code: String
) : Serializable