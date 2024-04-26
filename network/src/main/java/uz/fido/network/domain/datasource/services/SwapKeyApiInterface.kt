package uz.fido.network.domain.datasource.services

import retrofit2.Call
import retrofit2.http.*
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.abc_base.UserInfo
import uz.fido.network.domain.model.payment.Payment

interface SwapKeyApiInterface {

    @POST("v1/swapKey")
    suspend fun swapKeys(
        @Body request: SwapKeysRequest
    ): SwapKeysResponse

    @POST("v1/swapKey")
    fun swapKey(
        @Body request: SwapKeysRequest
    ): Call<SwapKeysResponse>

    @GET("GET_PAYMENT_FILE")
    suspend fun getPaymentFile(
        @Header("Authorization") token: String
    ): Payment

    @GET
    suspend fun getUserDetailedInfoAsync(@Url fileUrl: String): UserInfo

    @GET
    fun getUserDetailedInfo(@Url fileUrl: String): Call<UserInfo>

}