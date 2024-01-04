package uz.fido.network.domain.datasource.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface SocketInterface {

    @GET("chatTask")
    suspend fun testSocket(
        @Header("Authorization") token: String,
        @Header("id") deviceId: String
    ): Any

    @GET("chatTask")
    fun socketTest(
        @Header("Authorization") token: String,
        @Header("id") deviceId: String
    ): Call<BgTaskResponse>

    data class BgTaskResponse(
        val data: ArrayList<BgTask>
    )

    data class BgTask(
        val method: String? = null,
        val responses: String? = null
    )
}