package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IServiceRepository
import uz.fido.network.domain.datasource.services.ServiceApiInterface
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
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(private val serviceApi: ServiceApiInterface) :
    IServiceRepository {


    override suspend fun getCardOrderTypes(
        token: String,
        orderCardTypeRequest: OrderCardTypeRequest
    ): Resource<OrderCardTypeResponse> = getResult {
        serviceApi.getCardOrderTypes(token, orderCardTypeRequest)
    }

    override suspend fun orderCardRequest(
        token: String,
        orderCardRequest: OrderCardRequest
    ): Resource<BaseResponse> = getResult {
        serviceApi.orderCardRequest(token, orderCardRequest)
    }

    override suspend fun getMibInfo(
        token: String,
        mibInfoRequest: MibInfoRequest
    ): Resource<MibDetailsResponse> = getResult {
        serviceApi.getMibInfo(token, mibInfoRequest)
    }

    override suspend fun getMibPassportList(token: String): Resource<MibPassportListResponse> = getResult {
        serviceApi.getMibPassportList(token)
    }

    override suspend fun addMibPassport(
        token: String,
        addMibPassportRequest: AddMibPassportRequest
    ): Resource<BaseResponse> = getResult {
        serviceApi.addMibPassport(token, addMibPassportRequest)
    }

    override suspend fun deleteMibAccount(
        token: String,
        deleteMibAccount: DeleteMibAccount
    ): Resource<BaseResponse> = getResult {
        serviceApi.deleteMibAccount(token, deleteMibAccount)
    }

    override suspend fun getUserProductList(token: String): Resource<ApplicationsResponse> = getResult {
        serviceApi.getUserProductList(token)
    }

    override suspend fun getProductDetails(
        token: String,
        getProductDetailsRequest: GetProductDetailsRequest
    ): Resource<ProductDetailsResponse> = getResult {
        serviceApi.getProductDetails(token, getProductDetailsRequest)
    }

    override suspend fun orderVirtualCard(
        token: String,
        orderVirtualCardRequest: OrderVirtualCardRequest
    ): Resource<BaseResponse> = getResult {
        serviceApi.orderVirtualCard(token, orderVirtualCardRequest)
    }

    override suspend fun setTarget(token: String, request: SetTargetRequest): Resource<BaseResponse> = getResult {
        serviceApi.setTarget(token, request)
    }


    override suspend fun getTargetList(token: String): Resource<GoalListResponse> = getResult {
        serviceApi.getTargetList(token)
    }

    override suspend fun changeTargetState(
        token: String,
        request: ChangeTargetStateRequest
    ): Resource<BaseResponse> = getResult {
        serviceApi.changeTargetState(token, request)
    }

    override suspend fun editGoal(token: String, request: EditGoalRequest): Resource<BaseResponse> = getResult {
        serviceApi.editGoal(token, request)
    }


    override suspend fun targetHistories(
        token: String,
        request: ChangeTargetStateRequest
    ): Resource<GoalHistoriesResponse> = getResult {
        serviceApi.targetHistories(token, request)
    }

    override suspend fun getTargetInfo(
        token: String,
        request: ChangeTargetStateRequest
    ): Resource<GoalModelResponse> = getResult {
        serviceApi.getTargetInfo(token, request)
    }
}