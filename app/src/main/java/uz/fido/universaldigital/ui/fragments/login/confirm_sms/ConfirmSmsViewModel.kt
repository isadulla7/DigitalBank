package uz.fido.universaldigital.ui.fragments.login.confirm_sms

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ICardRepository
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.network.domain.datasource.interfaces.IPaymentRepository
import uz.fido.network.domain.datasource.interfaces.ISwapKeyRepository
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.cards.AddCardRequest
import uz.fido.network.domain.model.cards.ResetPinCount
import uz.fido.network.domain.model.home.GlSMSActivateRequest
import uz.fido.network.domain.model.p2p.P2PRequest
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sign_in.SignInRequest
import uz.fido.network.domain.model.sign_up.CheckUserSms
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.network.domain.model.sms.SendEmailCode
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class ConfirmSmsViewModel @Inject constructor(
    application: Application,
    private val userRepository: IUserRepository,
    private val swapKeyRepository: ISwapKeyRepository,
    private val cardRepository: ICardRepository,
    private val paymentRepository: IPaymentRepository,
    private val p2PRepository: IP2PRepository,
    private val utilsRepository: IUtilsRepository,
) : AbstractViewModel(application) {

    fun signIn(signInRequest: SignInRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.signIn(signInRequest))
    }

    fun getUserDetailedInfo(fileUrl: String) = liveData(Dispatchers.IO) {
        emit(swapKeyRepository.getUserDetailedInfoAsync(fileUrl))
    }

    fun finishReg(finishRegRequest: FinishRegRequest) = liveData(Dispatchers.IO) {
        emit(userRepository.finishReg(finishRegRequest))
    }


    fun checkUserSms(checkUserSms: CheckUserSms) = liveData(Dispatchers.IO) {
        emit(userRepository.checkUserSms(checkUserSms))
    }

    fun glSMSActivate(token: String, request: GlSMSActivateRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.glSMSActivate(token, request))
    }

    fun checkForSmsPaymentRequest(token: String, checkSmsForPayment: CheckSmsForPayment) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.checkSmsForPayment(token, checkSmsForPayment))
        }

    fun sendEmailCode(sendEmailCode: SendEmailCode) = liveData(Dispatchers.IO) {
        emit(userRepository.sendEmailCode(sendEmailCode))
    }

    fun p2pRequest(token: String, p2PRequest: P2PRequest) = liveData(Dispatchers.IO) {
        emit(p2PRepository.p2p(token, p2PRequest))
    }

    fun addCard(token: String, addCardRequest: AddCardRequest) = liveData(Dispatchers.IO) {
        emit(cardRepository.addCard(token, addCardRequest))
    }
    fun resetPinCount(token: String,  resetPinCount: ResetPinCount) = liveData(Dispatchers.IO) {
        emit(cardRepository.resetPinCount(token, resetPinCount))
    }

    fun terminateSession(token: String, deleteUserDeviceRequest: DeleteUserDeviceRequest) =
        liveData(Dispatchers.IO) {
            emit(utilsRepository.terminateSession(token, deleteUserDeviceRequest))
        }

    fun loanRepayment(token: String, createPaymentRequest: CreatePaymentRequest) =
        liveData(Dispatchers.IO) {
            emit(paymentRepository.loanRepayment(token, createPaymentRequest))
        }
}