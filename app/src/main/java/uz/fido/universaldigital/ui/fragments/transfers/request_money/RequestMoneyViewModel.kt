package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class RequestMoneyViewModel @Inject constructor(
    application: Application
) : AbstractViewModel(application)