package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
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

interface IUtilsRepository {

    suspend fun getSearchData(
        token: String, searchRequest: GetInfoRequest
    ): Resource<SearchDataResponse>

    suspend fun getCurrencyRates(
        token: String, getCurrencyRatesRequest: GetCurrencyRatesRequest
    ): Resource<CurrencyRatesResponse>

    suspend fun getBranchList(
        token: String, getBranchListRequest: GetBranchListRequest
    ): Resource<BranchListResponse>

    suspend fun getNewsList(
        token: String, getNewsList: GetNewsRequest
    ): Resource<NewsListResponse>

    suspend fun getNotifications(
        token: String, request: GetNotificationsRequest
    ): Resource<NotificationHistoryResponse>

    suspend fun getActiveSessions(
        token: String,
        userDevicesRequest: GetUserDevicesRequest
    ): Resource<GetUserDevicesResponse>

    suspend fun terminateSession(
        token: String,
        deleteUserDeviceRequest: DeleteUserDeviceRequest
    ): Resource<BaseResponse>

    suspend fun checkDevice(
        token: String,
        checkDeviceRequest: CheckDeviceRequest
    ): Resource<BaseResponse>

    suspend fun updateNotificationStatus(
        token: String,
        updateNotificationState: UpdateNotificationState
    ): Resource<BaseResponse>


}