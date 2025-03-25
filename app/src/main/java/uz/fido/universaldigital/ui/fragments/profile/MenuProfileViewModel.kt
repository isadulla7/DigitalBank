package uz.fido.universaldigital.ui.fragments.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.abc_base.ChangeNotifStateRequest
import uz.fido.network.domain.model.branches.Branches
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuProfileViewModel @Inject constructor(
    application: Application,
    private val userRepository: IUserRepository,
    private val utilsRepository: IUtilsRepository
) : AbstractViewModel(application) {

    var branches: MutableLiveData<List<Branches>> = MutableLiveData()

    fun updateBranches(branches: List<Branches>) {
        this.branches.postValue(branches)
    }

    fun logOutRequest(token: String, logOutRequest: LogOutRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.logOut(token, logOutRequest))
    }

    fun changeNotificationState(token: String, request: ChangeNotifStateRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.changeNotificationState(token, request))
    }

    fun getBranches(token: String, getBranchListRequest: GetBranchListRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getBranchList(token, getBranchListRequest))
    }

    fun editUserInfo(token: String, editUserInfo: EditUserInfo) = liveData(Dispatchers.IO) {
        emit(userRepository.editUserInfo(token, editUserInfo))
    }

    fun deleteAccount(token: String) = liveData(Dispatchers.IO) {
        emit(userRepository.deleteAccount(token))
    }

}