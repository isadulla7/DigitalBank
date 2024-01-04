package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.account.ChangePhoneNumberRequest
import uz.fido.network.domain.model.branches.BranchListResponse
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.contacts.CheckContactsRequest
import uz.fido.network.domain.model.contacts.CheckContactsResponse
import uz.fido.network.domain.model.customer.*
import uz.fido.network.domain.model.news.*
import uz.fido.network.domain.model.rates.CurrencyRatesResponse
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.network.domain.model.search.GetInfoRequest
import uz.fido.network.domain.model.search.SearchDataResponse
import uz.fido.network.domain.model.search.SearchRequest
import uz.fido.network.domain.model.search.SearchResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesResponse

interface UtilsApiInterface {
    @POST("SEARCH")
    suspend fun searchRequest(
        @Header("Authorization") token: String,
        @Body searchRequest: SearchRequest
    ): SearchResponse

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

    @POST("NEWS_IS_READ")
    suspend fun updateNewsStatus(
        @Header("Authorization") token: String,
        @Body updateNewsStatusRequest: UpdateNewsStatusRequest
    ): BaseResponse

    @POST("CHANGE_PHONE_NUMBER")
    suspend fun changePhoneNumber(
        @Header("Authorization") token: String,
        @Body request: ChangePhoneNumberRequest
    ): BaseResponse

    @POST("GET_NOTIFICATION_HIS")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Body request: GetNotificationsRequest
    ): NotificationHistoryResponse

    @POST("GET_RATING")
    suspend fun getRatingList(
        @Header("Authorization") token: String,
        @Body request: GetRatingsRequest
    ): RatingsResponse

    @GET("GET_CALL_URL")
    suspend fun getCallUrl(
        @Header("Authorization") token: String
    ): CallUrlResponse

    @POST("SEND_CALL_USER")
    suspend fun sendCallUser(
        @Header("Authorization") token: String,
        @Body request: SendCallUserRequest
    ): BaseResponse

    @POST("CALL_GET_CONTACT_LIST")
    suspend fun getInviteList(
        @Header("Authorization") token: String,
        @Body request: GetRatingsRequest
    ): InviteListResponse

    @POST("CALC_SCORE")
    suspend fun calcScore(
        @Header("Authorization") token: String,
        @Body request: CalcScoreRequest
    ): BaseResponse

    @POST("CHECK_CONTACTS")
    suspend fun checkContactList(
        @Header("Authorization") token: String,
        @Body checkContactsRequest: CheckContactsRequest
    ): CheckContactsResponse

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