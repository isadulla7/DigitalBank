package uz.fido.universaldigital.ui.fragments.monitoring

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.network.domain.model.monitoring.filter.FilterSaveVh
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class MenuMonitoringViewModel @Inject constructor(
    application: Application
) : AbstractViewModel(application) {

    val uzcardList = MutableLiveData<ArrayList<String>>()
    val humoList = MutableLiveData<ArrayList<String>>()
    val walledList = MutableLiveData<ArrayList<String>>()
    val allCardList = MutableLiveData<Boolean>()
    val currencyList = MutableLiveData<ArrayList<String>>()
    val localMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val uzCardMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val humoMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val visaMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val walletMonitoringFilter = MutableLiveData<FilterSaveVh>()
    val saveLocalMonitoring = MutableLiveData<ArrayList<LocalMonitoring>>()
    var isFilterWindows = false

    var localFilter = false
    var uzCardFilter = false
    var humoFilter = false
    var walletFilter = false
    var visaFilter = false
    var saveLocalMonitoringCurrent = false

    fun setLocalMonitoringFilter(saveFilter: FilterSaveVh) {
        localMonitoringFilter.value = saveFilter
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

    fun saveLocalMonitoring(list: ArrayList<LocalMonitoring>) {
        saveLocalMonitoring.value = list
    }

}