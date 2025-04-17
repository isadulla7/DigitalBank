package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.branches.BranchListResponse
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.news.GetNewsRequest
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.news.NewsListResponse
import uz.fido.network.domain.model.news.NotificationHistoryResponse
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.network.domain.model.rates.CurrencyRatesResponse
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesResponse

interface UtilsApiInterface {

    @POST("GET_SEARCH_REQ_DATA")
    suspend fun getSearchData(
        @Header("Authorization") token: String,
        @Body searchRequest: GetInfoRequest
    ): SearchDataResponse

    @POST("GET_CURRENCY_RATES")
    suspend fun getCurrencyRates(
        @Header("Authorization") token: String,
        @Body getCurrencyRatesRequest: GetCurrencyRatesRequest
    ): CurrencyRatesResponse

    @POST("GET_FILIALS")
    suspend fun getBranchList(
        @Header("Authorization") token: String,
        @Body getBranchListRequest: GetBranchListRequest
    ): BranchListResponse

    @POST("GET_NEWS")
    suspend fun getNewsList(
        @Header("Authorization") token: String,
        @Body getNewsList: GetNewsRequest
    ): NewsListResponse

    @POST("GET_NOTIFICATION_HIS")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Body request: GetNotificationsRequest
    ): NotificationHistoryResponse

    @POST("GET_DEVICES_LIST")
    suspend fun getActiveSessions(
        @Header("Authorization") token: String, @Body getUserDevicesRequest: GetUserDevicesRequest
    ): GetUserDevicesResponse

    @POST("SEND_SMS_FOR_REMOVE_DEVICE")
    suspend fun checkDeviceRequest(
        @Header("Authorization") token: String, @Body checkDeviceRequest: CheckDeviceRequest
    ): BaseResponse

    @POST("REMOVE_USER_DEVICE")
    suspend fun deleteSession(
        @Header("Authorization") token: String, @Body deleteUserDeviceRequest: DeleteUserDeviceRequest
    ): BaseResponse

    @POST("NOTIFICATION_IS_READ")
    suspend fun updateNotificationStatus(
        @Header("Authorization") token: String,
        @Body updateNewsStatusRequest: UpdateNotificationState
    ): BaseResponse

}