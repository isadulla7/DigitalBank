package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistoriesResponse
import uz.fido.network.domain.model.monitoring.categories.SetCategoryRequest
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringRequest
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringResponse
import uz.fido.network.domain.model.monitoring.filter.MonitoringFilterCardResponse
import uz.fido.network.domain.model.monitoring.filter.NewFilterMonitoringFilterRequest
import uz.fido.network.domain.model.monitoring.filter.PaymentServiceResponse
import uz.fido.network.domain.model.monitoring.home.HomeHistoryRequest
import uz.fido.network.domain.model.monitoring.home.HomeHistoryResponse
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringRequest
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringResponse
import uz.fido.network.domain.model.monitoring.local.GetLocalHistoryRequest
import uz.fido.network.domain.model.monitoring.local.LocalHistoryResponse
import uz.fido.network.domain.model.monitoring.local.NewMonitoringFilterRequest
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringRequest
import uz.fido.network.domain.model.monitoring.uzcard.SvMonitoringOldResponse
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringResponse

interface IMonitoringRepository {

    suspend fun getUzcardMonitoringOld(
        token: String, svMonitoringRequest: SVMonitoringRequest
    ): Resource<SvMonitoringOldResponse>

    suspend fun getHumoMonitoring(
        token: String, humoMonitoringRequest: HumoMonitoringRequest
    ): Resource<HumoMonitoringResponse>

    suspend fun getLocalMonitoring(
        token: String, localMonitoring: LocalMonitoringRequest
    ): Resource<LocalMonitoringResponse>

    suspend fun getLocaleHistories(
        token: String, getLocaleHistoryRequest: GetLocalHistoryRequest
    ): Resource<LocalHistoryResponse>

    suspend fun getMonitoringCategories(
        token: String
    ): Resource<BaseResponse>

    suspend fun setMonitoringCategory(
        token: String, setCategoryRequest: SetCategoryRequest
    ): Resource<BaseResponse>

    suspend fun getCurrencyCardMonitoring(
        token: String, monitoringRequest: CurrencyCardMonitoringRequest
    ): Resource<CurrencyCardMonitoringResponse>

    suspend fun getAccountHistories(
        token: String, accountHistoriesRequest: AccountHistoriesRequest
    ): Resource<AccountHistoriesResponse>

    suspend fun getHistoryByAcc(
        token: String, request: HomeHistoryRequest
    ): Resource<HomeHistoryResponse>

    suspend fun filterLocalMonitoring(
        token: String,
        filterMonitoringModel: NewMonitoringFilterRequest
    ): Resource<LocalMonitoringResponse>

    suspend fun getLocalMonitoringCardList(token: String): Resource<MonitoringFilterCardResponse>

    suspend fun getLocalMonitoringServiceList(token: String): Resource<PaymentServiceResponse>

    suspend fun newFilterLocalMonitoring(
        token: String,
        filterMonitoringModel: NewFilterMonitoringFilterRequest
    ): Resource<LocalMonitoringResponse>
}