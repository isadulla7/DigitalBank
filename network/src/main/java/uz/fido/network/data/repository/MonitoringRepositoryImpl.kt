package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.repositories.IMonitoringRepository
import uz.fido.network.domain.datasource.services.MonitoringApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.monitoring.AccountHistoriesRequest
import uz.fido.network.domain.model.monitoring.AccountHistoriesResponse
import uz.fido.network.domain.model.monitoring.categories.SetCategoryRequest
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringRequest
import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringResponse
import uz.fido.network.domain.model.monitoring.home.HomeHistoryRequest
import uz.fido.network.domain.model.monitoring.home.HomeHistoryResponse
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringRequest
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringResponse
import uz.fido.network.domain.model.monitoring.local.GetLocalHistoryRequest
import uz.fido.network.domain.model.monitoring.local.LocalHistoryResponse
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringRequest
import uz.fido.network.domain.model.monitoring.uzcard.SvMonitoringOldResponse
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringResponse
import javax.inject.Inject

class MonitoringRepositoryImpl @Inject constructor(private val monitoringApiService: MonitoringApiInterface) :
    IMonitoringRepository {
    override suspend fun getUzcardMonitoringOld(
        token: String,
        svMonitoringRequest: SVMonitoringRequest
    ): Resource<SvMonitoringOldResponse> = getResult {
        monitoringApiService.getUzcardMonitoringOld(token, svMonitoringRequest)
    }

    override suspend fun getHumoMonitoring(
        token: String,
        humoMonitoringRequest: HumoMonitoringRequest
    ): Resource<HumoMonitoringResponse> = getResult {
        monitoringApiService.getHumoMonitoring(token, humoMonitoringRequest)
    }

    override suspend fun getLocalMonitoring(
        token: String,
        localMonitoring: LocalMonitoringRequest
    ): Resource<LocalMonitoringResponse> = getResult {
        monitoringApiService.getLocalMonitoring(token, localMonitoring)
    }

    override suspend fun getLocaleHistories(
        token: String,
        getLocaleHistoryRequest: GetLocalHistoryRequest
    ): Resource<LocalHistoryResponse> = getResult {
        monitoringApiService.getLocaleHistories(token, getLocaleHistoryRequest)
    }

    override suspend fun getMonitoringCategories(token: String): Resource<BaseResponse> =
        getResult {
            monitoringApiService.getMonitoringCategories(token)
        }

    override suspend fun setMonitoringCategory(
        token: String,
        setCategoryRequest: SetCategoryRequest
    ): Resource<BaseResponse> = getResult {
        monitoringApiService.setMonitoringCategory(token, setCategoryRequest)
    }

    override suspend fun getCurrencyCardMonitoring(
        token: String,
        monitoringRequest: CurrencyCardMonitoringRequest
    ): Resource<CurrencyCardMonitoringResponse> = getResult {
        monitoringApiService.getCurrencyCardMonitoring(token, monitoringRequest)
    }

    override suspend fun getAccountHistories(
        token: String,
        accountHistoriesRequest: AccountHistoriesRequest
    ): Resource<AccountHistoriesResponse> = getResult {
        monitoringApiService.getAccountHistories(token, accountHistoriesRequest)
    }

    override suspend fun getHistoryByAcc(
        token: String,
        request: HomeHistoryRequest
    ): Resource<HomeHistoryResponse> = getResult {
        monitoringApiService.getHistoryByAcc(token, request)
    }
}