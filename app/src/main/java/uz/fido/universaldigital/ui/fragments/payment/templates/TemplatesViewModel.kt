package uz.fido.universaldigital.ui.fragments.payment.templates

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    application: Application
) : AbstractViewModel(application)