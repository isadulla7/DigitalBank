package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IWalletRepository
import uz.fido.network.domain.datasource.services.WalletApiInterface
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.wallet.CreateWalletRequest
import uz.fido.network.domain.model.wallet.DeleteWalletRequest
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

}