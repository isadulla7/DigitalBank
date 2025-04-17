package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
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

interface IServiceRepository {

    suspend fun getCardOrderTypes(
        token: String, orderCardTypeRequest: OrderCardTypeRequest
    ): Resource<OrderCardTypeResponse>

    suspend fun orderCardRequest(
        token: String, orderCardRequest: OrderCardRequest
    ): Resource<BaseResponse>

    suspend fun getMibInfo(
        token: String, mibInfoRequest: MibInfoRequest
    ): Resource<MibDetailsResponse>

    suspend fun getMibPassportList(
        token: String
    ): Resource<MibPassportListResponse>

    suspend fun addMibPassport(
        token: String, addMibPassportRequest: AddMibPassportRequest
    ): Resource<BaseResponse>

    suspend fun deleteMibAccount(
        token: String, deleteMibAccount: DeleteMibAccount
    ): Resource<BaseResponse>

    suspend fun getUserProductList(
        token: String
    ): Resource<ApplicationsResponse>

    suspend fun getProductDetails(
        token: String, getProductDetailsRequest: GetProductDetailsRequest
    ): Resource<ProductDetailsResponse>

    suspend fun orderVirtualCard(
        token: String, orderVirtualCardRequest: OrderVirtualCardRequest
    ): Resource<BaseResponse>

    suspend fun setTarget(
        token: String, request: SetTargetRequest
    ): Resource<BaseResponse>

    suspend fun getTargetList(
        token: String
    ): Resource<GoalListResponse>

    suspend fun changeTargetState(
        token: String, request: ChangeTargetStateRequest
    ): Resource<BaseResponse>

    suspend fun editGoal(
        token: String, request: EditGoalRequest
    ): Resource<BaseResponse>

    suspend fun targetHistories(
        token: String, request: ChangeTargetStateRequest
    ): Resource<GoalHistoriesResponse>

    suspend fun getTargetInfo(
        token: String, request: ChangeTargetStateRequest
    ): Resource<GoalModelResponse>

}