package uz.fido.network.domain.datasource.interfaces

import retrofit2.Call
import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.payment.Payment

interface ISwapKeyRepository {

    suspend fun swapKeys(
        request: SwapKeysRequest
    ): Resource<SwapKeysResponse>

    fun swapKey(
        request: SwapKeysRequest
    ): Call<SwapKeysResponse>

    suspend fun getPaymentFile(token: String): Resource<Payment>

    suspend fun getUserDetailedInfoAsync(fileUrl: String): Resource<UserInfo>

    fun getUserDetailedInfo(fileUrl: String): Call<UserInfo>
}