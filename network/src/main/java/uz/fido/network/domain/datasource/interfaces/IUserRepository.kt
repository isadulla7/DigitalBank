package uz.fido.network.domain.datasource.interfaces

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.abc_base.ChangeNotifStateRequest
import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenRequest
import uz.fido.network.domain.model.my_id.MyIdMeResponse
import uz.fido.network.domain.model.password.ChangePasswordRequest
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.network.domain.model.sign_in.ResidencyResponse
import uz.fido.network.domain.model.sign_in.SignInRequestNew
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.network.domain.model.sign_up.CheckUserSms
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.network.domain.model.sign_up.SignUpCheckRequest
import uz.fido.network.domain.model.sms.SendEmailCode

interface IUserRepository {

    suspend fun deleteAccount(token: String): Resource<BaseResponse>

    suspend fun signIn(signInRequestNew: SignInRequestNew): Resource<SignInResponse>

    suspend fun signInPin(
        token: String,
        signInRequestNew: SignInRequestNew
    ): Resource<SignInResponse>

    suspend fun checkUserSms(
        checkUserSms: CheckUserSms
    ): Resource<SignInResponse>

    suspend fun signUpCheck(
        signUpCheckRequest: SignUpCheckRequest
    ): Resource<BaseResponse>

    suspend fun finishReg(finishRegRequest: FinishRegRequest): Resource<SignInResponse>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Resource<BaseResponse>

    suspend fun changePasswordWithoutSMS(
        token: String, changePasswordRequest: ChangePasswordRequest
    ): Resource<BaseResponse>

    suspend fun logOut(
        token: String, logOutRequest: LogOutRequest
    ): Resource<BaseResponse>

    suspend fun checkIdentification(
        token: String, userIdentification: CheckIdentification
    ): Resource<BaseResponse>

    suspend fun getAccessToken(
        clientToken: String,
        myIdGetAccessTokenRequest: MyIdGetAccessTokenRequest
    ): Resource<MyIdMeResponse>

    suspend fun editUserInfo(
        token: String, editUserInfo: EditUserInfo
    ): Resource<BaseResponse>

    suspend fun sendEmailCode(
        sendEmailCode: SendEmailCode
    ): Resource<SignInResponse>

    suspend fun changeNotificationState(
        token: String,
        request: ChangeNotifStateRequest
    ): Resource<BaseResponse>

    suspend fun getUserResidency(
        token: String
    ): Resource<ResidencyResponse>

}