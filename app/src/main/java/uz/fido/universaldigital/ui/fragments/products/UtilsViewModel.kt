package uz.fido.universaldigital.ui.fragments.products

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.network.data.repository.CardRepositoryImpl
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.datasource.interfaces.IServiceRepository
import uz.fido.network.domain.datasource.interfaces.ITemplateRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.applications.GetProductDetailsRequest
import uz.fido.network.domain.model.cards.CheckCardRequest
import uz.fido.network.domain.model.cards.ResetPinCountCheck
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.network.domain.model.template.DeleteTemplateRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.SetTemplateOrderRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

@HiltViewModel
class UtilsViewModel @Inject constructor(
    application: Application,
    private val utilsRepository: IUtilsRepository,
    private val serviceRepository: IServiceRepository,
    private val templatesRepository: ITemplateRepository,
    private val p2PRepository: IP2PRepository,
    private val cardsUseCase: CardsUseCase,
    private val iCardRepository: ICardRepository,
    private val cardRepository: CardRepositoryImpl
) : AbstractViewModel(application) {

    var shouldTemplateUpdate = true

    var currencyRates: MutableLiveData<List<CourseItem>> = MutableLiveData()
    var templates: MutableLiveData<List<Template>> = MutableLiveData()
    var myHouse: MutableLiveData<ArrayList<MyHouseGroup>> = MutableLiveData()
    var popularTransfers: MutableLiveData<List<PopularTransfers>> = MutableLiveData()

    fun updateRates() {
        vmScope.launch {
            val rates = cardsUseCase.getRates(context.getClientToken())
            currencyRates.postValue(rates)
        }
    }

    fun updateRates(rates: List<CourseItem>) {
        currencyRates.postValue(rates)
    }

    fun updateTemplates(templateList: List<Template>) {
        templates.postValue(templateList)
    }
    fun updateMyHouse(home:ArrayList<MyHouseGroup>) {
        myHouse.postValue(home)
    }

    fun updatePopularTransfers(transfers: List<PopularTransfers>) {
        popularTransfers.postValue(transfers)
    }

    fun getCurrencyRates(token: String, getCurrencyRatesRequest: GetCurrencyRatesRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getCurrencyRates(token, getCurrencyRatesRequest))
    }

    fun getTemplateList(token: String, getTemplateListRequest: GetTemplateListRequest) = liveData(Dispatchers.IO) {
        emit(templatesRepository.getTemplateList(token, getTemplateListRequest))
    }
    fun getTemplateGroups(token: String) = liveData(Dispatchers.IO) {
        emit(templatesRepository.getTemplateGroup(token))
    }

    fun getUserAppList(token: String) = liveData(Dispatchers.IO) {
        emit(serviceRepository.getUserProductList(token))
    }

    fun getProductDetails(token: String, getProductDetailsRequest: GetProductDetailsRequest) = liveData(Dispatchers.IO) {
        emit(serviceRepository.getProductDetails(token, getProductDetailsRequest))
    }

    fun getPopularTransferList(token: String) = liveData(Dispatchers.IO) {
        emit(p2PRepository.getPopularTransferList(token))
    }

    fun getTemplate(token: String, getTemplateRequest: GetTemplateRequest) = liveData(Dispatchers.IO) {
        emit(templatesRepository.getTemplate(token, getTemplateRequest))
    }

    fun deleteTemplate(token: String, deleteTemplateRequest: DeleteTemplateRequest) = liveData(Dispatchers.IO) {
        emit(templatesRepository.deleteTemplate(token, deleteTemplateRequest))
    }

    fun createTemplate(token: String, createTemplateRequest: CreateTemplateRequest) = liveData(Dispatchers.IO) {
        emit(templatesRepository.createTemplate(token, createTemplateRequest))
    }

    fun setTemplateOrder(token: String, setTemplateOrderRequest: SetTemplateOrderRequest) = liveData(Dispatchers.IO) {
        emit(templatesRepository.setTemplateOrder(token, setTemplateOrderRequest))
    }

    fun checkResetPinCount(token: String, resetPinCountCheck: ResetPinCountCheck) = liveData(Dispatchers.IO) {
        emit(iCardRepository.checkResetPinCount(token, resetPinCountCheck))
    }

    fun checkCardRequest(token: String, checkCardRequest: CheckCardRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.checkCard(token, checkCardRequest))
    }


}