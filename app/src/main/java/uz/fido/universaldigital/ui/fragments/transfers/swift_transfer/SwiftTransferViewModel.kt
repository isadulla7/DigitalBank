package uz.fido.universaldigital.ui.fragments.transfers.swift_transfer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.model.get_card_by_phone.CardByPhone
import uz.fido.network.domain.model.swift.CreateSwiftAppRequest
import uz.fido.network.domain.model.swift.GetSwiftCommissionRequest
import uz.fido.network.domain.model.swift.SwiftRequest
import uz.fido.network.domain.model.swift.SwiftTransferListRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class SwiftTransferViewModel @Inject constructor(
    application: Application,
    private val paymentRepository: IPaymentRepository
) : AbstractViewModel(application) {

    var histories: MutableLiveData<List<CardByPhone>> = MutableLiveData()

    fun updateHistory(histories: List<CardByPhone>) {
        this.histories.postValue(histories)
    }

    fun getSwiftCommission(token: String, getSwiftCommissionRequest: GetSwiftCommissionRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.getSwiftCommission(token, getSwiftCommissionRequest))
        }

    fun getSwiftBic(token: String, swiftRequest: SwiftRequest) = liveData(Dispatchers.IO) {
        emit(paymentRepository.getSwiftBic(token, swiftRequest))
    }

    fun createSwiftApp(token: String, swiftAppRequest: CreateSwiftAppRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.createSwiftApp(token, swiftAppRequest))
        }

    fun getTransferList(token: String, swiftTransferListRequest: SwiftTransferListRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.getSwiftDocs(token, swiftTransferListRequest))
        }

}