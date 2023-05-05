package uz.fido.network.domain.datasource.services

import retrofit2.http.GET
import retrofit2.http.Header

interface SocketInterface {

    @GET("chatTask")
    suspend fun testSocket(
        @Header("Authorization") token: String
    ): Any

}