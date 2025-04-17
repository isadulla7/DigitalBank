package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.network.domain.datasource.interfaces.IP2PRepository
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class RequestMoneyViewModel @Inject constructor(
    application: Application,
    private val p2pRepository: IP2PRepository
) : AbstractViewModel(application) {

}