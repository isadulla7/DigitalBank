package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IMyIdRepository
import uz.fido.network.domain.datasource.services.MyIdApiInterface
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenResponse
import uz.fido.network.domain.model.my_id.MyIdMeResponse
import javax.inject.Inject

class MyIdRepositoryImpl @Inject constructor(private val    myIdService: MyIdApiInterface) :
    IMyIdRepository {

    override suspend fun getAccessTokenMyId(
        grant_type: String,
        code: String,
        client_id: String,
        client_secret: String,
        redirect_url: String
    ): Resource<MyIdGetAccessTokenResponse> = getResult {
        myIdService.getAccessTokenMyId(grant_type, code, client_id, client_secret, redirect_url)
    }

    override suspend fun getMyIdMe(token: String): Resource<MyIdMeResponse> = getResult {
        myIdService.getMyIdMe(token)
    }
}