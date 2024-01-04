package uz.fido.universaldigital.ui.fragments.login.sign_up

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ISwapKeyRepository
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val userRepository: IUserRepository,
    private val swapKeyRepository: ISwapKeyRepository
) : AbstractViewModel(application) {

    fun swapKeys(request: SwapKeysRequest) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.swapKeys(request))
    }

    fun getUserDetailedInfo(fileUrl: String) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.getUserDetailedInfoAsync(fileUrl))
    }

    fun checkSignUpRequest(signUpCheckRequest: SignUpCheckRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.signUpCheck(signUpCheckRequest))
    }

}