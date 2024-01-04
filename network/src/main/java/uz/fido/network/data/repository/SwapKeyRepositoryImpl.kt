package uz.fido.network.data.repository

import retrofit2.Call
import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.ISwapKeyRepository
import uz.fido.network.domain.datasource.services.SwapKeyApiInterface
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.payment.Payment
import javax.inject.Inject

class SwapKeyRepositoryImpl @Inject constructor(private val swapKeyService: SwapKeyApiInterface) :
    ISwapKeyRepository {
    override suspend fun swapKeys(request: SwapKeysRequest): Resource<SwapKeysResponse> =
        getResult {
            swapKeyService.swapKeys(request)
        }

    override fun swapKey(request: SwapKeysRequest): Call<SwapKeysResponse> =
        swapKeyService.swapKey(request)

    override suspend fun getPaymentFile(token: String): Resource<Payment> = getResult {
        swapKeyService.getPaymentFile(token)
    }

    override suspend fun getUserDetailedInfoAsync(fileUrl: String): Resource<UserInfo> = getResult {
        swapKeyService.getUserDetailedInfoAsync(fileUrl)
    }

    override fun getUserDetailedInfo(fileUrl: String): Call<UserInfo> = swapKeyService.getUserDetailedInfo(fileUrl)

}