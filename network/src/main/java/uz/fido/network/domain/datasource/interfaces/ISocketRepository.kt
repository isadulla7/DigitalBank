package uz.fido.network.domain.datasource.interfaces

import retrofit2.Call
import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.datasource.services.SocketInterface

interface ISocketRepository {

    suspend fun getMessages(
        token: String,
        deviceId: String
    ): Resource<Call<SocketInterface.BgTaskResponse>>

}