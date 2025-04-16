package uz.fido.universaldigital.ui.fragments.services.mib

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IServiceRepository
import uz.fido.network.domain.model.mib.AddMibPassportRequest
import uz.fido.network.domain.model.mib.DeleteMibAccount
import uz.fido.network.domain.model.mib.MibInfoRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MibViewModel @Inject constructor(
    application: Application,
    private val serviceRepository: IServiceRepository
) : AbstractViewModel(application) {

    fun addMibPassport(token: String, addMibPassportRequest: AddMibPassportRequest) = liveData(
        Dispatchers.IO
    ) {
        emit(serviceRepository.addMibPassport(token, addMibPassportRequest))
    }

    fun getMibPassportList(token: String) = liveData(Dispatchers.IO) {
        emit(serviceRepository.getMibPassportList(token))
    }

    fun getMibInfo(token: String, mibInfoRequest: MibInfoRequest) = liveData(Dispatchers.IO) {
        emit(serviceRepository.getMibInfo(token, mibInfoRequest))
    }

    fun deleteMibAccount(token: String, deleteMibAccount: DeleteMibAccount) =
        liveData(Dispatchers.IO) {
            emit(serviceRepository.deleteMibAccount(token, deleteMibAccount))
        }

}