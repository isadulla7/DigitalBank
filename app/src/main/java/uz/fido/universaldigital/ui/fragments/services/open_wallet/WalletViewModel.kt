package uz.fido.universaldigital.ui.fragments.services.open_wallet

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IWalletRepository
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    application: Application,
    private val walletRepository: IWalletRepository,
) : AbstractViewModel(application) {

    fun createWallet(token: String, createWalletRequest: CreateWalletRequest) =
        liveData(Dispatchers.IO) {
            emit(walletRepository.createWallet(token, createWalletRequest))
        }

}