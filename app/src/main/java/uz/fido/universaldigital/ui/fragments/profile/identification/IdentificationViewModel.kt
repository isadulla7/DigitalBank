package uz.fido.universaldigital.ui.fragments.profile.identification

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class IdentificationViewModel @Inject constructor(
    application: Application, private val userRepository: IUserRepository
) : AbstractViewModel(application) {

    fun identification(token: String, checkIdentification: CheckIdentification) =
        liveData(Dispatchers.IO) {
            emit(userRepository.checkIdentification(token, checkIdentification))
        }

    fun checkPassport(myIdGetAccessTokenRequest: MyIdGetAccessTokenRequest) =
        liveData(Dispatchers.IO) {
            emit(userRepository.getAccessToken(myIdGetAccessTokenRequest))
        }

}