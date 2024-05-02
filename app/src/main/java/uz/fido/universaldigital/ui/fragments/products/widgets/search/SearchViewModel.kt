package uz.fido.universaldigital.ui.fragments.products.widgets.search

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application
) : AbstractViewModel(application)