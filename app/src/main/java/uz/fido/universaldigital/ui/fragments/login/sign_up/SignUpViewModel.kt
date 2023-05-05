package uz.fido.universaldigital.ui.fragments.login.sign_up

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.data.repository.UserRepositoryImpl
import uz.fido.network.domain.model.sign_in.SignInRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val userRepositoryImpl: UserRepositoryImpl
) : AbstractViewModel(application) {

    fun signIn(signInRequest: SignInRequest) = liveData(Dispatchers.IO) {
        emit(userRepositoryImpl.signIn(signInRequest))
    }

}