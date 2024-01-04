package uz.fido.universaldigital.ui.fragments.login.restore_profile

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.model.password.ChangePasswordRequest
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sms.SendEmailCode
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class RestoreProfileViewModel @Inject constructor(
    application: Application,
    private val userRepository: IUserRepository,
) : AbstractViewModel(application) {

    fun finishReg(finishRegRequest: FinishRegRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.finishReg(finishRegRequest))
    }

    fun sendEmailCode(sendEmailCode: SendEmailCode) = liveData(Dispatchers.IO) {
        emit(userRepository.sendEmailCode(sendEmailCode))
    }

    fun changePassword(token: String, changePasswordRequest: ChangePasswordRequest) =
        liveData(Dispatchers.IO) {
            emit(userRepository.changePasswordWithoutSMS(token, changePasswordRequest))
        }

    fun changePasswordWithSMS(changePasswordRequest: ChangePasswordRequest) =
        liveData(Dispatchers.IO) {
            emit(userRepository.changePassword(changePasswordRequest))
        }


}