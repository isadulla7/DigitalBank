package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest

interface WalletApiInterface {

    @POST("CREATE_PURSE")
    suspend fun createWallet(
        @Header("Authorization") token: String,
        @Body createWalletRequest: CreateWalletRequest
    ): BaseResponse

    @POST("CLOSE_PURSE")
    suspend fun deleteWallet(
        @Header("Authorization") token: String,
        @Body deleteWalletRequest: DeleteWalletRequest
    ): BaseResponse

}