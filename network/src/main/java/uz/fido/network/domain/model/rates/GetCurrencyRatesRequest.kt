package uz.fido.network.domain.model.rates

data class GetCurrencyRatesRequest(
    val command: String,
    val version_name: String,
    val exchange_rate_date: String? = ""
)