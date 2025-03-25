package uz.fido.universaldigital.ui.fragments.profile.security

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MyDevicesViewModel @Inject constructor(
    application: Application,
    private val utilsRepository: IUtilsRepository
) : AbstractViewModel(application) {

    fun getActiveSessions(token: String, getUserDevicesRequest: GetUserDevicesRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getActiveSessions(token, getUserDevicesRequest))
    }

    fun checkDevice(token: String, checkDeviceRequest: CheckDeviceRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.checkDevice(token, checkDeviceRequest))
    }

}