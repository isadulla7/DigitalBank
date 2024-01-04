package uz.fido.network.domain.datasource.services

import retrofit2.Call
import retrofit2.http.GET
import uz.fido.network.domain.model.rates.CurrencyRatesResponse

interface RatesApi {

    @GET("GET_CURRENCY_RATES_OUT")
    fun getCurrencyRates(): Call<CurrencyRatesResponse>

}