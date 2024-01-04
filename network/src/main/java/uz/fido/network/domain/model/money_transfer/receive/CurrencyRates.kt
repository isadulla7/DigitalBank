package uz.fido.network.domain.model.money_transfer.receive

import java.io.Serializable

data class CurrencyRates(
    val rate: String,
    val from_currency: String,
    val to_currency: String
) : Serializable