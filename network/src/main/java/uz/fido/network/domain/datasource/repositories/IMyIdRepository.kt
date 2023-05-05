package uz.fido.network.domain.datasource.repositories

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenResponse
import uz.fido.network.domain.model.my_id.MyIdMeResponse

interface IMyIdRepository {

    suspend fun getAccessTokenMyId(
        grant_type: String,
        code: String,
        client_id: String,
        client_secret: String,
        redirect_url: String,
    ): Resource<MyIdGetAccessTokenResponse>

    suspend fun getMyIdMe(token: String): Resource<MyIdMeResponse>

}