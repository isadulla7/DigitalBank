package uz.fido.universaldigital.ui.fragments.profile.identification

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ISwapKeyRepository
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenRequest
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

@HiltViewModel
class IdentificationViewModel @Inject constructor(
    application: Application,
    private val userRepository: IUserRepository,
    private val swapKeyRepository: ISwapKeyRepository
) : AbstractViewModel(application) {

    fun identification(token: String, checkIdentification: CheckIdentification) = liveData(Dispatchers.IO) {
        emit(userRepository.checkIdentification(token, checkIdentification))
    }

    fun checkPassport(myIdGetAccessTokenRequest: MyIdGetAccessTokenRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.getAccessToken(context.getClientToken(), myIdGetAccessTokenRequest))
    }

    fun signIn(signInRequest: SignInRequestNew) = liveData(Dispatchers.IO) {
        emit(userRepository.signInPin(context.getClientToken(), signInRequest))
    }

    fun swapKeysPin(request: SwapKeysRequest) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.swapKeysPin(request))
    }

    fun getUserDetailedInfo(fileUrl: String) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.getUserDetailedInfoAsync(fileUrl))
    }

}