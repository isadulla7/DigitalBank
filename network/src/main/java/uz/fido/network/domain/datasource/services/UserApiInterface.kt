package uz.fido.network.domain.datasource.services

import retrofit2.Call
import retrofit2.http.*
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.ChangeNotifStateRequest
import uz.fido.network.domain.model.abc_base.SwapKeysRequest
import uz.fido.network.domain.model.abc_base.SwapKeysResponse
import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.network.domain.model.my_id.*
import uz.fido.network.domain.model.password.ChangePasswordRequest
import uz.fido.network.domain.model.password.CheckForgetPasswordModel
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.DeleteUserDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesResponse
import uz.fido.network.domain.model.sign_in.SignInRequest
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.network.domain.model.sign_up.CheckUserSms
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.network.domain.model.sign_up.SignUpRequest
import uz.fido.network.domain.model.sms.SendEmailCode

interface UserApiInterface {

    @GET("DELETE_ACCOUNT")
    suspend fun deleteAccount(
        @Header("Authorization") token: String,
    ): BaseResponse

    @POST("GET_ACCESS_TOKEN")
    suspend fun getAccessTokenMyId(
        @Header("Authorization") token: String,
        @Body myIdGetAccessTokenRequest: MyIdGetAccessTokenRequest
    ): MyIdMeResponse

    @POST("USER_SIGN_IN_NEW")
    suspend fun signIn(
        @Body signInRequest: SignInRequestNew
    ): SignInResponse

    @POST("USER_SIGN_IN_NEW")
    suspend fun signInPin(
        @Header("Authorization") token: String,
        @Body signInRequest: SignInRequestNew
    ): SignInResponse

    @POST("USER_SIGN_IN_NEW")
    fun signInNew(
        @Header("Authorization") token: String,
        @Body signInRequest: SignInRequestNew
    ): Call<SignInResponse>

    @POST("CHECK_SMS_CODE")
    suspend fun checkUserSms(
        @Body checkUserSms: CheckUserSms
    ): SignInResponse

    @POST("USER_SIGN_IN")
    suspend fun signIn(
        @Body signInRequest: SignInRequest
    ): SignInResponse

    @POST("USER_START_REG")
    suspend fun signUpCheck(
        @Header("device_type") deviceType: String,
        @Body signUpCheckRequest: SignUpCheckRequest
    ): BaseResponse

    @POST("USER_REG")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): SignInResponse

    @POST("USER_FINISH_REG")
    suspend fun finishReg(
        @Body signUpRequest: FinishRegRequest
    ): SignInResponse

    @POST("CHECK_FORGOT_PASSWORD")
    suspend fun checkForgetPassword(@Body checkForgetPasswordModel: CheckForgetPasswordModel): BaseResponse

    @POST("CHANGE_FORGOT_PASSWORD")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): BaseResponse

    @POST("CHANGE_PASSWORD")
    suspend fun changePassWithoutSMS(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangePasswordRequest
    ): BaseResponse

    @POST("LOG_OUT_NEW")
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

    @POST("v1/swapKey")
    suspend fun swapKeys(
        @Body request: SwapKeysRequest
    ): SwapKeysResponse

    @POST("SEND_EMAIL_CODE")
    suspend fun sendEmailCode(
        @Body sendSmsEmailCode: SendEmailCode
    ): SignInResponse

    @POST("CHANGE_USER_NOTIF_STATE")
    suspend fun changeNotificationState(
        @Header("Authorization") token: String,
        @Body request: ChangeNotifStateRequest
    ): BaseResponse

}