package uz.fido.universaldigital.ui.fragments.products

import uz.fido.network.data.utility.Status
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.utils.const.Command
import javax.inject.Inject

interface CardsUseCase {
    suspend fun getRates(clientToken: String): ArrayList<CourseItem>
}

class CardsUseCaseImpl @Inject constructor(
    private val utilsRepository: IUtilsRepository
) : CardsUseCase {

    override suspend fun getRates(clientToken: String): ArrayList<CourseItem> {
        val response = utilsRepository.getCurrencyRates(
            clientToken,
            GetCurrencyRatesRequest(Command.INFO, "all")
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.currency_rates ?: ArrayList()
        } else ArrayList()
    }

}