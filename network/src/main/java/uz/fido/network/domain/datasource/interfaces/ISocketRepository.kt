package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource

interface ISocketRepository {

    suspend fun testSocket(token: String, deviceId:String): Resource<Any>

}