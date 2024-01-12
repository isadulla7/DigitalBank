package uz.fido.universaldigital.widgets.currency_rates.retrofit

import android.os.Handler
import android.os.Looper

class RatesRepository() : Repository {

    override fun getData(callback: (RepoResult) -> Unit) {

        val uiHandler = Handler(Looper.getMainLooper())
        val service = RatesRetrofit().foo
//        val call = service.getCurrencyRates()

//        call.enqueue(object : Callback<CurrencyRatesResponse> {
//            override fun onFailure(call: Call<CurrencyRatesResponse>, t: Throwable) {
//                uiHandler.post {
//                    callback(Failure("Could not get data, probably network"))
//                }
//            }
//
//            override fun onResponse(
//                call: Call<CurrencyRatesResponse>, response: Response<CurrencyRatesResponse>
//            ) {
//                val data = response.body()
//                data ?: uiHandler.post {
//                    callback(Failure("Api returned no data"))
//                }
//                data?.let {
//                    uiHandler.post {
//                        if (it.currency_rates != null)
//                            callback(Success(it.currency_rates!!))
//                    }
//                }
//            }
//        })
    }

}