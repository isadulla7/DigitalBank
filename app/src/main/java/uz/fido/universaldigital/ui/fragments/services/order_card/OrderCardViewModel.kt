package uz.fido.universaldigital.ui.fragments.services.order_card

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IServiceRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.branches.Branches
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.cards.OrderCardRequest
import uz.fido.network.domain.model.cards.OrderCardTypeRequest
import uz.fido.network.domain.model.cards.OrderVirtualCardRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class OrderCardViewModel @Inject constructor(
    application: Application,
    private val serviceRepository: IServiceRepository,
    private val utilsRepository: IUtilsRepository
) : AbstractViewModel(application) {

    var templates: MutableLiveData<List<Template>> = MutableLiveData()
    var branches: MutableLiveData<List<Branches>> = MutableLiveData()

    var shouldBranchListUpdate = true

    fun updateBranches(branches: List<Branches>) {
        this.branches.postValue(branches)
    }

    fun getProductTypes(token: String, orderCardTypeRequest: OrderCardTypeRequest) =
        liveData(Dispatchers.IO) {
            emit(serviceRepository.getCardOrderTypes(token, orderCardTypeRequest))
        }

    fun getBranches(token: String, getBranchListRequest: GetBranchListRequest) =
        liveData(Dispatchers.IO) {
            emit(utilsRepository.getBranchList(token, getBranchListRequest))
        }

    fun orderCard(token: String, orderCardRequest: OrderCardRequest) = liveData(Dispatchers.IO) {
        emit(serviceRepository.orderCardRequest(token, orderCardRequest))
    }

    fun orderVirtualCard(token: String, orderVirtualCard: OrderVirtualCardRequest) =
        liveData(Dispatchers.IO) {
            emit(serviceRepository.orderVirtualCard(token, orderVirtualCard))
        }

}