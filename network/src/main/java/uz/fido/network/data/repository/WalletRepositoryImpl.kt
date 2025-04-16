package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IWalletRepository
import uz.fido.network.domain.datasource.services.WalletApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
import uz.fido.network.domain.model.wallet.RenameWalletRequest
import uz.fido.network.domain.model.wallet.WalletDataRequest
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(private val walletService: WalletApiInterface) :
    IWalletRepository {
    override suspend fun createWallet(
        token: String,
        createWalletRequest: CreateWalletRequest
    ): Resource<BaseResponse> = getResult {
        walletService.createWallet(token, createWalletRequest)
    }

    override suspend fun deleteWallet(
        token: String,
        deleteWalletRequest: DeleteWalletRequest
    ): Resource<BaseResponse> = getResult {
        walletService.deleteWallet(token, deleteWalletRequest)
    }

    override suspend fun getPurseData(
        token: String,
        getWalletData: WalletDataRequest
    ): Resource<BaseResponse> = getResult {
        walletService.getPurseData(token, getWalletData)
    }

    override suspend fun renameWallet(
        token: String,
        renameWalletRequest: RenameWalletRequest
    ): Resource<BaseResponse> = getResult {
        walletService.renameWallet(token, renameWalletRequest)
    }

    override suspend fun setWalletState(
        token: String,
        renameWalletRequest: RenameWalletRequest
    ): Resource<BaseResponse> = getResult {
        walletService.setWalletState(token, renameWalletRequest)
    }
}