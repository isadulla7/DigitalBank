package uz.fido.network.domain.datasource.services

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse

interface MonitoringApiInterface {

    @POST("GET_SV_CARD_TRAN_HIS")
    suspend fun getUzcardMonitoringOld(
        @Header("Authorization") token: String,
        @Body svMonitoringRequest: SVMonitoringRequest
    ): SvMonitoringOldResponse

    @POST("GET_HUMO_MONITORING")
    suspend fun getHumoMonitoring(
        @Header("Authorization") token: String,
        @Body humoMonitoringRequest: HumoMonitoringRequest
    ): HumoMonitoringResponse

    @POST("GET_LOCAL_TRAN_HISTORY")
    suspend fun getLocalMonitoring(
        @Header("Authorization") token: String, @Body localMonitoring: LocalMonitoringRequest
    ): LocalMonitoringResponse

    @POST("GET_USER_OBJ_LOCAL_HIS")
    suspend fun getLocaleHistories(
        @Header("Authorization") token: String,
        @Body getLocaleHistoryRequest: GetLocalHistoryRequest
    ): LocalHistoryResponse

    @GET("GET_MONITORING_CATEGORIES")
    suspend fun getMonitoringCategories(
        @Header("Authorization") token: String
    ): BaseResponse

    @POST("SET_MONITORING_CATEGORY")
    suspend fun setMonitoringCategory(
        @Header("Authorization") token: String,
        @Body setCategoryRequest: SetCategoryRequest
    ): BaseResponse

    @POST("GET_TET_MONITORING")
    suspend fun getCurrencyCardMonitoring(
        @Header("Authorization") token: String,
        @Body monitoringRequest: CurrencyCardMonitoringRequest
    ): CurrencyCardMonitoringResponse

    @POST("GET_IBS_ACCOUNT_TURNOVER")
    suspend fun getAccountHistories(
        @Header("Authorization") token: String,
        @Body accountHistoriesRequest: AccountHistoriesRequest
    ): AccountHistoriesResponse

    @POST("GET_HIS_BY_RECEIVER_ACC")
    suspend fun getHistoryByAcc(
        @Header("Authorization") token: String,
        @Body request: HomeHistoryRequest
    ): HomeHistoryResponse

}