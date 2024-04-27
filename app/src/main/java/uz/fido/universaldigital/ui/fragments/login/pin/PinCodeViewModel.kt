package uz.fido.universaldigital.ui.fragments.login.pin

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ISwapKeyRepository
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

@HiltViewModel
class PinCodeViewModel @Inject constructor(
    application: Application,
    private val userRepository: IUserRepository,
    private val swapKeyRepository: ISwapKeyRepository
) : AbstractViewModel(application) {

    fun signIn(signInRequest: SignInRequestNew) = liveData(Dispatchers.IO) {
        emit(userRepository.signInPin(getClientToken(), signInRequest))
    }

    fun swapKeys(request: SwapKeysRequest) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.swapKeys(request))
    }

    fun swapKeysPin(request: SwapKeysRequest) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.swapKeysPin(request))
    }

    fun getUserDetailedInfo(fileUrl: String) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.getUserDetailedInfoAsync(fileUrl))
    }

    fun logOutRequest(token: String, logOutRequest: LogOutRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.logOut(token, logOutRequest))
    }

}