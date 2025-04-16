package uz.fido.universaldigital.ui.fragments.monitoring

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uz.fido.network.domain.model.monitoring.filter.FilterSaveVh
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuMonitoringViewModel @Inject constructor(
    application: Application
) : AbstractViewModel(application) {

    private var _userHasCard = MutableStateFlow(false)
    val userHasCard: StateFlow<Boolean> = _userHasCard

    private var _saveLocalMonitoring = MutableStateFlow<ArrayList<LocalMonitoring>>(arrayListOf())
    val saveLocalMonitoring: StateFlow<ArrayList<LocalMonitoring>> = _saveLocalMonitoring

    private var _localMonitoringFilter = MutableStateFlow(FilterSaveVh())
    val localMonitoringFilter: StateFlow<FilterSaveVh> = _localMonitoringFilter

    val uzcardList = MutableLiveData<ArrayList<String>>()
    val humoList = MutableLiveData<ArrayList<String>>()
    val walledList = MutableLiveData<ArrayList<String>>()

    val currencyList = MutableLiveData<ArrayList<String>>()
    val uzCardMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val humoMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val visaMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val walletMonitoringFilter = MutableLiveData<FilterSaveVh>()
    var isFilterWindows = false

    var localFilter = false
    var uzCardFilter = false
    var humoFilter = false
    var walletFilter = false
    var visaFilter = false
    var saveLocalMonitoringCurrent = false

    fun userHasCard(value: Boolean) {
        _userHasCard.value = value
    }

    fun saveLocalMonitoring(list: ArrayList<LocalMonitoring>) {
        _saveLocalMonitoring.value = list
    }

    fun setLocalMonitoringFilter(saveFilter: FilterSaveVh) {
        _localMonitoringFilter.value = saveFilter
    }


    fun setUzCardMonitoringFilter(saveFilter: FilterSaveVh) {
        uzCardMonitoringFilter.value = saveFilter
    }

    fun setHumoMonitoringFilter(saveFilter: FilterSaveVh) {
        humoMonitoringFilter.value = saveFilter
    }

    fun setVisaMonitoringFilter(saveFilter: FilterSaveVh) {
        visaMonitoringFilter.value = saveFilter
    }

    fun setWalletMonitoringFilter(saveFilter: FilterSaveVh) {
        walletMonitoringFilter.value = saveFilter
    }


}