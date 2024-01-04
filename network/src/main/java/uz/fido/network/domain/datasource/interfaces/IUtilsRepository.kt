package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.account.ChangePhoneNumberRequest
import uz.fido.network.domain.model.branches.BranchListResponse
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.contacts.CheckContactsRequest
import uz.fido.network.domain.model.contacts.CheckContactsResponse
import uz.fido.network.domain.model.customer.CalcScoreRequest
import uz.fido.network.domain.model.customer.CallUrlResponse
import uz.fido.network.domain.model.customer.GetRatingsRequest
import uz.fido.network.domain.model.customer.InviteListResponse
import uz.fido.network.domain.model.customer.RatingsResponse
import uz.fido.network.domain.model.customer.SendCallUserRequest
import uz.fido.network.domain.model.news.GetNewsRequest
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.news.NewsListResponse
import uz.fido.network.domain.model.news.NotificationHistoryResponse
import uz.fido.network.domain.model.news.UpdateNewsStatusRequest
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.network.domain.model.rates.CurrencyRatesResponse
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.network.domain.model.search.SearchRequest
import uz.fido.network.domain.model.search.SearchResponse
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesResponse

interface IUtilsRepository {

    suspend fun searchRequest(
        token: String, searchRequest: SearchRequest
    ): Resource<SearchResponse>

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

    suspend fun updateNewsStatus(
        token: String, updateNewsStatusRequest: UpdateNewsStatusRequest
    ): Resource<BaseResponse>

    suspend fun changePhoneNumber(
        token: String, request: ChangePhoneNumberRequest
    ): Resource<BaseResponse>

    suspend fun getNotifications(
        token: String, request: GetNotificationsRequest
    ): Resource<NotificationHistoryResponse>

    suspend fun getRatingList(
        token: String, request: GetRatingsRequest
    ): Resource<RatingsResponse>

    suspend fun getCallUrl(
        token: String
    ): Resource<CallUrlResponse>

    suspend fun sendCallUser(
        token: String, request: SendCallUserRequest
    ): Resource<BaseResponse>

    suspend fun getInviteList(
        token: String, request: GetRatingsRequest
    ): Resource<InviteListResponse>

    suspend fun calcScore(
        token: String, request: CalcScoreRequest
    ): Resource<BaseResponse>

    suspend fun checkContactList(
        token: String, checkContactsRequest: CheckContactsRequest
    ): Resource<CheckContactsResponse>

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