package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.applications.ApplicationsResponse
import uz.fido.network.domain.model.applications.GetProductDetailsRequest
import uz.fido.network.domain.model.applications.ProductDetailsResponse
import uz.fido.network.domain.model.cards.OrderCardRequest
import uz.fido.network.domain.model.cards.OrderCardTypeRequest
import uz.fido.network.domain.model.cards.OrderCardTypeResponse
import uz.fido.network.domain.model.cards.OrderVirtualCardRequest
import uz.fido.network.domain.model.mib.AddMibPassportRequest
import uz.fido.network.domain.model.mib.DeleteMibAccount
import uz.fido.network.domain.model.mib.MibDetailsResponse
import uz.fido.network.domain.model.mib.MibInfoRequest
import uz.fido.network.domain.model.mib.MibPassportListResponse
import uz.fido.network.domain.model.target.ChangeTargetStateRequest
import uz.fido.network.domain.model.target.EditGoalRequest
import uz.fido.network.domain.model.target.GoalHistoriesResponse
import uz.fido.network.domain.model.target.GoalListResponse
import uz.fido.network.domain.model.target.GoalModelResponse
import uz.fido.network.domain.model.target.SetTargetRequest

interface ServiceApiInterface {

    @POST("GET_CARD_ORDER_TYPES")
    suspend fun getCardOrderTypes(
        @Header("Authorization") token: String,
        @Body orderCardTypeRequest: OrderCardTypeRequest
    ): OrderCardTypeResponse

    @POST("ORDER_CARD")
    suspend fun orderCardRequest(
        @Header("Authorization") token: String,
        @Body orderCardRequest: OrderCardRequest
    ): BaseResponse

    @POST("GET_MIB_INFO")
    suspend fun getMibInfo(
        @Header("Authorization") token: String,
        @Body mibInfoRequest: MibInfoRequest
    ): MibDetailsResponse

    @GET("GET_MIB_PASSPORT_LIST")
    suspend fun getMibPassportList(
        @Header("Authorization") token: String
    ): MibPassportListResponse

    @POST("ADD_MIB_PASSPORT")
    suspend fun addMibPassport(
        @Header("Authorization") token: String,
        @Body addMibPassportRequest: AddMibPassportRequest
    ): BaseResponse

    @POST("DELETE_MIB_ACCOUNT")
    suspend fun deleteMibAccount(
        @Header("Authorization") token: String,
        @Body deleteMibAccount: DeleteMibAccount
    ): BaseResponse

    @GET("GET_USER_PRODUCT_LIST")
    suspend fun getUserProductList(
        @Header("Authorization") token: String
    ): ApplicationsResponse

    @POST("GET_PRODUCT_DETAILS")
    suspend fun getProductDetails(
        @Header("Authorization") token: String,
        @Body getProductDetailsRequest: GetProductDetailsRequest
    ): ProductDetailsResponse

    @POST("ORDER_CARD_VIRTUAL")
    suspend fun orderVirtualCard(
        @Header("Authorization") token: String,
        @Body orderVirtualCardRequest: OrderVirtualCardRequest
    ): BaseResponse

    @POST("TR_SET_TARGET")
    suspend fun setTarget(
        @Header("Authorization") token: String,
        @Body request: SetTargetRequest
    ): BaseResponse

    @GET("TR_GET_USER_TARGET_LIST")
    suspend fun getTargetList(
        @Header("Authorization") token: String
    ): GoalListResponse

    @POST("TR_CHANGE_TARGET_STATE")
    suspend fun changeTargetState(
        @Header("Authorization") token: String,
        @Body request: ChangeTargetStateRequest
    ): BaseResponse

    @POST("TR_EDIT_TARGET")
    suspend fun editGoal(
        @Header("Authorization") token: String,
        @Body request: EditGoalRequest
    ): BaseResponse

    @POST("TR_GET_USER_TARGET_TRANSFER")
    suspend fun targetHistories(
        @Header("Authorization") token: String,
        @Body request: ChangeTargetStateRequest
    ): GoalHistoriesResponse

    @POST("TR_GET_USER_TARGET_INFO")
    suspend fun getTargetInfo(
        @Header("Authorization") token: String,
        @Body request: ChangeTargetStateRequest
    ): GoalModelResponse

}