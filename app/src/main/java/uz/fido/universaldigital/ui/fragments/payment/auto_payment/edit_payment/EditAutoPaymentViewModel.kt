package uz.fido.universaldigital.ui.fragments.payment.auto_payment.edit_payment

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class EditAutoPaymentViewModel @Inject constructor(
    application: Application
) : AbstractViewModel(application)