package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.ISocketRepository
import uz.fido.network.domain.datasource.services.SocketInterface
import javax.inject.Inject

class SocketRepositoryImpl @Inject constructor(private val socketService: SocketInterface) :
    ISocketRepository {
    override suspend fun testSocket(token: String, deviceId: String): Resource<Any> = getResult {
        socketService.testSocket(token, deviceId)
    }

}