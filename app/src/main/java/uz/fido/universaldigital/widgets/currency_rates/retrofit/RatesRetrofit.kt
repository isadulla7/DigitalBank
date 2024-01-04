package uz.fido.universaldigital.widgets.currency_rates.retrofit

import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.fido.network.domain.datasource.services.RatesApi
import uz.fido.universaldigital.app.UniversalApplication.Companion.getContext

class RatesRetrofit {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RequestInterface {
        val ratesApi: RatesApi
    }

    val myClassInterface =
        EntryPoints.get(getContext(), RequestInterface::class.java)
    val foo = myClassInterface.ratesApi
}