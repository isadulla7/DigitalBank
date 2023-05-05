package uz.fido.network.domain.datasource.repositories

import uz.fido.network.data.utility.Resource

interface ISocketRepository {

    suspend fun testSocket(token: String): Resource<Any>

}