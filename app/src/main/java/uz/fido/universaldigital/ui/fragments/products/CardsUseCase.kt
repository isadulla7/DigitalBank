package uz.fido.universaldigital.ui.fragments.products

import uz.fido.network.data.utility.Status
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.cards.CardInfo
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.utils.const.Command
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

interface CardsUseCase {
    suspend fun getCardList(): ArrayList<CardResponse>
    suspend fun getCardInfo(objectId: ArrayList<String>): ArrayList<CardInfo>
    suspend fun getRates(): ArrayList<CourseItem>
}

class CardsUseCaseImpl @Inject constructor(
    private val cardRepository: ICardRepository,
    private val utilsRepository: IUtilsRepository
) : CardsUseCase {

    override suspend fun getCardList(): ArrayList<CardResponse> {
        val response = cardRepository.getCardList(getClientToken())
        return if (response.status == Status.SUCCESS) {
            response.data?.objects ?: ArrayList()
        } else ArrayList()
    }

    override suspend fun getCardInfo(objectId: ArrayList<String>): ArrayList<CardInfo> {
        val response = cardRepository.getCardInfo(
            getClientToken(),
            CardInfoRequest(object_ids = objectId)
        )
        if (response.status == Status.SUCCESS) {
            response.data.let {
                if (it?.objects?.isNotEmpty() == true) {
                    return it.objects!!
                } else return arrayListOf()
            }
        } else {
            val objInfoList = ArrayList<CardInfo>()
            objectId.forEach { _ ->
                objInfoList.add(CardInfo(state = "-100"))
            }
            return objInfoList
        }
    }

    override suspend fun getRates(): ArrayList<CourseItem> {
        val response = utilsRepository.getCurrencyRates(
            getClientToken(),
            GetCurrencyRatesRequest(Command.INFO, "all")
        )
        return if (response.status == Status.SUCCESS) {
            response.data?.currency_rates ?: ArrayList()
        } else ArrayList()
    }

}