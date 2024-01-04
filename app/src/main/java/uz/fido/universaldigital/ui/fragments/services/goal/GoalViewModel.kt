package uz.fido.universaldigital.ui.fragments.services.goal

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.datasource.interfaces.IServiceRepository
import uz.fido.network.domain.model.p2p.P2PInfoRequest
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.target.ChangeTargetStateRequest
import uz.fido.network.domain.model.target.EditGoalRequest
import uz.fido.network.domain.model.target.SetTargetRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    application: Application,
    private val serviceRepository: IServiceRepository,
    private val p2PRepository: IP2PRepository
) : AbstractViewModel(application) {

    fun getTargetList(token: String) = liveData(Dispatchers.IO) {
        emit(serviceRepository.getTargetList(token))
    }

    fun setTarget(token: String, setTargetRequest: SetTargetRequest) = liveData(Dispatchers.IO) {
        emit(serviceRepository.setTarget(token, setTargetRequest))
    }

    fun targetHistories(token: String, request: ChangeTargetStateRequest) =
        liveData(Dispatchers.IO) {
            emit(serviceRepository.targetHistories(token, request))
        }

    fun getTargetInfo(token: String, request: ChangeTargetStateRequest) = liveData(Dispatchers.IO) {
        emit(serviceRepository.getTargetInfo(token, request))
    }

    fun targetTransfer(token: String, p2PRequest: P2PRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.targetTransfer(token, p2PRequest))
    }

    fun p2pInfoRequest(token: String, p2PInfoRequest: P2PInfoRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.p2pInfoRequest(token, p2PInfoRequest))
    }

    fun changeTargetState(token: String, request: ChangeTargetStateRequest) =
        liveData(Dispatchers.IO) {
            emit(serviceRepository.changeTargetState(token, request))
        }

    fun editGoal(token: String, request: EditGoalRequest) = liveData(Dispatchers.IO) {
        emit(serviceRepository.editGoal(token, request))
    }

    fun closeTarget(token: String, p2PRequest: P2PRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.closeTarget(token, p2PRequest))
    }
}