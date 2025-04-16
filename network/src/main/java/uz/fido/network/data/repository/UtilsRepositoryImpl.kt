package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.datasource.services.UtilsApiInterface
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
import javax.inject.Inject

class UtilsRepositoryImpl @Inject constructor(private val utilsService: UtilsApiInterface) :
    IUtilsRepository {

    override suspend fun searchRequest(
        token: String, searchRequest: SearchRequest
    ): Resource<SearchResponse> = getResult {
        utilsService.searchRequest(token, searchRequest)
    }

    override suspend fun getSearchData(
        token: String, searchRequest: GetInfoRequest
    ): Resource<SearchDataResponse> = getResult {
        utilsService.getSearchData(token, searchRequest)
    }

    override suspend fun getCurrencyRates(
        token: String, getCurrencyRatesRequest: GetCurrencyRatesRequest
    ): Resource<CurrencyRatesResponse> = getResult {
        utilsService.getCurrencyRates(token, getCurrencyRatesRequest)
    }

    override suspend fun getBranchList(
        token: String, getBranchListRequest: GetBranchListRequest
    ): Resource<BranchListResponse> = getResult {
        utilsService.getBranchList(token, getBranchListRequest)
    }

    override suspend fun getNewsList(
        token: String, getNewsList: GetNewsRequest
    ): Resource<NewsListResponse> = getResult {
        utilsService.getNewsList(token, getNewsList)
    }

    override suspend fun updateNewsStatus(
        token: String, updateNewsStatusRequest: UpdateNewsStatusRequest
    ): Resource<BaseResponse> = getResult {
        utilsService.updateNewsStatus(token, updateNewsStatusRequest)
    }

    override suspend fun changePhoneNumber(
        token: String, request: ChangePhoneNumberRequest
    ): Resource<BaseResponse> = getResult {
        utilsService.changePhoneNumber(token, request)
    }

    override suspend fun getNotifications(
        token: String, request: GetNotificationsRequest
    ): Resource<NotificationHistoryResponse> = getResult {
        utilsService.getNotifications(token, request)
    }

    override suspend fun getRatingList(
        token: String, request: GetRatingsRequest
    ): Resource<RatingsResponse> = getResult {
        utilsService.getRatingList(token, request)
    }


    override suspend fun getCallUrl(token: String): Resource<CallUrlResponse> = getResult {
        utilsService.getCallUrl(token)
    }

    override suspend fun sendCallUser(
        token: String, request: SendCallUserRequest
    ): Resource<BaseResponse> = getResult {
        utilsService.sendCallUser(token, request)
    }


    override suspend fun getInviteList(
        token: String, request: GetRatingsRequest
    ): Resource<InviteListResponse> = getResult {
        utilsService.getInviteList(token, request)
    }

    override suspend fun calcScore(
        token: String, request: CalcScoreRequest
    ): Resource<BaseResponse> = getResult {
        utilsService.calcScore(token, request)

    }

    override suspend fun checkContactList(
        token: String, checkContactsRequest: CheckContactsRequest
    ): Resource<CheckContactsResponse> = getResult {
        utilsService.checkContactList(token, checkContactsRequest)
    }

    override suspend fun getActiveSessions(
        token: String, userDevicesRequest: GetUserDevicesRequest
    ): Resource<GetUserDevicesResponse> = getResult {
        utilsService.getActiveSessions(token, userDevicesRequest)
    }

    override suspend fun terminateSession(
        token: String, deleteUserDeviceRequest: DeleteUserDeviceRequest
    ): Resource<BaseResponse> =
        getResult { utilsService.deleteSession(token, deleteUserDeviceRequest) }

    override suspend fun checkDevice(
        token: String, checkDeviceRequest: CheckDeviceRequest
    ): Resource<BaseResponse> = getResult {
        utilsService.checkDeviceRequest(token, checkDeviceRequest)
    }

    override suspend fun updateNotificationStatus(
        token: String,
        updateNotificationState: UpdateNotificationState
    ): Resource<BaseResponse> = getResult {
        utilsService.updateNotificationStatus(token, updateNotificationState)
    }

}