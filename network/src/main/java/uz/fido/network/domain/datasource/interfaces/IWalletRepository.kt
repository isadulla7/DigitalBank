package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest

interface IWalletRepository {
    suspend fun createWallet(
        token: String, createWalletRequest: CreateWalletRequest
    ): Resource<BaseResponse>

    suspend fun deleteWallet(
        token: String, deleteWalletRequest: DeleteWalletRequest
    ): Resource<BaseResponse>

}