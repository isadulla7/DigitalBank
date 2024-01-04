package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenResponse
import uz.fido.network.domain.model.my_id.MyIdMeResponse
import retrofit2.http.*

interface MyIdApiInterface {

    @FormUrlEncoded
    @POST("api/v1/oauth2/access-token/")
    suspend fun getAccessTokenMyId(
        @Field("grant_type") grant_type: String,
        @Field("code") code: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("redirect_url") redirect_url: String,
    ): MyIdGetAccessTokenResponse

    @GET("api/v1/users/me")
    suspend fun getMyIdMe(@Header("Authorization") token: String): MyIdMeResponse



}