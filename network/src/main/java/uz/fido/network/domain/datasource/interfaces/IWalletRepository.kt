package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
import uz.fido.network.domain.model.wallet.RenameWalletRequest
import uz.fido.network.domain.model.wallet.WalletDataRequest

interface IWalletRepository {
    suspend fun createWallet(
        token: String, createWalletRequest: CreateWalletRequest
    ): Resource<BaseResponse>

    suspend fun deleteWallet(
        token: String, deleteWalletRequest: DeleteWalletRequest
    ): Resource<BaseResponse>

    suspend fun getPurseData(
        token: String, getWalletData: WalletDataRequest
    ): Resource<BaseResponse>

    suspend fun renameWallet(
        token: String, renameWalletRequest: RenameWalletRequest
    ): Resource<BaseResponse>

    suspend fun setWalletState(
        token: String, renameWalletRequest: RenameWalletRequest
    ): Resource<BaseResponse>
}