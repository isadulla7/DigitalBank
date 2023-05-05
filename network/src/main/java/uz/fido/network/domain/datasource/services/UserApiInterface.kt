package uz.fido.network.domain.datasource.services

import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.network.domain.model.liveness.CheckLivenessResponse
import uz.fido.network.domain.model.password.ChangePasswordRequest
import uz.fido.network.domain.model.password.CheckForgetPasswordModel
import uz.fido.network.domain.model.payment.Payment
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesResponse
import uz.fido.network.domain.model.sign_in.CheckUserSignInRequest
import uz.fido.network.domain.model.sign_in.SignInRequest
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.network.domain.model.sign_up.CheckUserSms
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.network.domain.model.sign_up.SignUpRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.GetPhysicalPhoto
import uz.fido.network.domain.model.my_id.GetPhysicalPhotoResponse
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenRequest
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenResponse
import uz.fido.network.domain.model.my_id.MyIdMeResponse

interface UserApiInterface {

    @POST("liveness")
    fun checkLiveness(@Body image: MultipartBody): Call<CheckLivenessResponse>

    @FormUrlEncoded
    @POST("api/v1/oauth2/access-token/")
    suspend fun getAccessTokenMyId(
        @Field("grant_type") grant_type: String,
        @Field("code") code: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("redirect_url") redirect_url: String,
    ): MyIdGetAccessTokenResponse

    @POST("GET_ACCESS_TOKEN")
    suspend fun getAccessTokenMyId(
        @Body myIdGetAccessTokenRequest: MyIdGetAccessTokenRequest
    ): MyIdGetAccessTokenResponse

    @GET("api/v1/users/me")
    suspend fun getMyIdMe(@Header("Authorization") token: String): MyIdMeResponse

    @GET
    fun downloadPayments(@Url fileUrl: String?): Call<ResponseBody>

    @GET("GET_PAYMENT_FILE")
    suspend fun getPaymentFile(
        @Header("Authorization") token: String
    ): Payment

    @POST("USER_SIGN_IN_CHECK")
    suspend fun checkUserSignIn(
        @Body requestBody: CheckUserSignInRequest
    ): BaseResponse

    @POST("CHECK_USER_SMS")
    suspend fun checkUserSms(
        @Body checkUserSms: CheckUserSms
    ): BaseResponse

    @POST("USER_SIGN_IN")
    suspend fun signIn(
        @Body signInRequest: SignInRequest
    ): SignInResponse

    @POST("USER_REG_CHECK")
    suspend fun signUpCheck(
        @Body signUpCheckRequest: SignUpCheckRequest
    ): BaseResponse

    @POST("USER_REG")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): SignInResponse

    @POST("CHECK_FORGOT_PASSWORD")
    suspend fun checkForgetPassword(@Body checkForgetPasswordModel: CheckForgetPasswordModel): BaseResponse

    @POST("CHANGE_FORGOT_PASSWORD")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): BaseResponse

    @POST("LOG_OUT")
    suspend fun logOut(
        @Header("Authorization") token: String,
        @Body logOutRequest: LogOutRequest
    ): BaseResponse

    @POST("GET_PHYSICAL_PHOTO")
    suspend fun getPhysicalPhoto(
        @Body getPhysicalPhoto: GetPhysicalPhoto
    ): GetPhysicalPhotoResponse

    @POST("USER_IDENTIFICATION")
    suspend fun checkIdentification(
        @Header("Authorization") token: String,
        @Body userIdentification: CheckIdentification
    ): BaseResponse

    @POST("EDIT_USER_INFO")
    suspend fun editUserInfo(
        @Header("Authorization") token: String,
        @Body editUserInfo: EditUserInfo
    ): BaseResponse

    @POST("GET_USER_DEVICES")
    suspend fun getActiveSessions(
        @Header("Authorization") token: String,
        @Body getUserDevicesRequest: GetUserDevicesRequest
    ): GetUserDevicesResponse

    @POST("CHECK_MANAGE_USER_DEVICE")
    suspend fun checkDeviceRequest(
        @Header("Authorization") token: String,
        @Body checkDeviceRequest: CheckDeviceRequest
    ): BaseResponse

    @POST("MANAGE_USER_DEVICE")
    suspend fun deleteSession(
        @Header("Authorization") token: String,
        @Body deleteUserDeviceRequest: DeleteUserDeviceRequest
    ): BaseResponse
}