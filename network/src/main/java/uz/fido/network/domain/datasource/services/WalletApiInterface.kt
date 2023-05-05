package uz.fido.network.domain.datasource.services

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
import uz.fido.network.domain.model.wallet.RenameWalletRequest
import uz.fido.network.domain.model.wallet.WalletDataRequest

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

    @POST("GET_PURSE_DATA")
    suspend fun getPurseData(
        @Header("Authorization") token: String,
        @Body getWalletData: WalletDataRequest
    ): BaseResponse

    @POST("RENAME_PURSE")
    suspend fun renameWallet(
        @Header("Authorization") token: String,
        @Body renameWalletRequest: RenameWalletRequest
    ): BaseResponse

    @POST("SET_PURSE_STATE")
    suspend fun setWalletState(
        @Header("Authorization") token: String,
        @Body renameWalletRequest: RenameWalletRequest
    ): BaseResponse
}