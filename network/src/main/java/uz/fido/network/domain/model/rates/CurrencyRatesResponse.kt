package uz.fido.network.domain.model.rates

data class CurrencyRatesResponse(
    val code: Int,
    val currency_rates: ArrayList<CourseItem>? = null,
    val ora_msg: String,
    val msg: String
)