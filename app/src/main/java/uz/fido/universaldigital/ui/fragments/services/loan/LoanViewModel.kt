package uz.fido.universaldigital.ui.fragments.services.loan

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.ICreditRepository
import uz.fido.network.domain.datasource.interfaces.IMyIdRepository
import uz.fido.network.domain.model.loans.CheckHasLoanRequest
import uz.fido.network.domain.model.loans.CreateCreditRequestNew
import uz.fido.network.domain.model.loans.LnSearchLoanRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    application: Application,
    private val creditRepository: ICreditRepository,
    private val myIdRepository: IMyIdRepository
) : AbstractViewModel(application) {

    fun getCreditGroups(token: String) = liveData(Dispatchers.IO) {
        emit(creditRepository.getCreditGroups(token))
    }

    fun checkLoanByCrm(token: String, request: CheckHasLoanRequest) = liveData(Dispatchers.IO) {
        emit(creditRepository.checkHasLoan(token, request))
    }

    fun getUserInfo(token: String) = liveData(Dispatchers.IO) {
        emit(creditRepository.getUserInfo(token))
    }

    fun getAccessToken(
        grant_type: String,
        code: String,
        client_id: String,
        client_secret: String,
        redirect_url: String
    ) = liveData(Dispatchers.IO) {
        emit(
            myIdRepository.getAccessTokenMyId(
                grant_type,
                code,
                client_id,
                client_secret,
                redirect_url
            )
        )
    }

    fun getMe(token: String) = liveData(Dispatchers.IO) {
        emit(myIdRepository.getMyIdMe(token))
    }

    fun getLnSearchLoan(token: String,lnSearchLoanRequest: LnSearchLoanRequest) = liveData(Dispatchers.IO) {
        emit(creditRepository.getLnSearchLoan(token,lnSearchLoanRequest))
    }

    fun createCreditRequest(token: String, createCreateCreditAppRequest: CreateCreditRequestNew) =
        liveData(Dispatchers.IO) {
            emit(creditRepository.createCreditRequest(token, createCreateCreditAppRequest))
        }
}