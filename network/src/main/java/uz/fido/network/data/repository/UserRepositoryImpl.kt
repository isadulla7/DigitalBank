package uz.fido.network.data.repository

import uz.fido.network.data.utility.Resource
import uz.fido.network.data.utility.getResult
import uz.fido.network.domain.datasource.interfaces.IUserRepository
import uz.fido.network.domain.datasource.services.UserApiInterface
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(private val userApiService: UserApiInterface) : IUserRepository {

    override suspend fun deleteAccount(token: String): Resource<BaseResponse> = getResult {
        userApiService.deleteAccount(token)
    }


    override suspend fun signIn(signInRequestNew: SignInRequestNew): Resource<SignInResponse> = getResult {
        userApiService.signIn(signInRequestNew)
    }

    override suspend fun checkUserSms(
        checkUserSms: CheckUserSms
    ): Resource<SignInResponse> = getResult {
        userApiService.checkUserSms(checkUserSms)
    }

    override suspend fun signInPin(
        token: String,
        signInRequestNew: SignInRequestNew
    ): Resource<SignInResponse> = getResult {
        userApiService.signInPin(token, signInRequestNew)
    }

    override suspend fun signUpCheck(
        signUpCheckRequest: SignUpCheckRequest
    ): Resource<BaseResponse> = getResult {
        userApiService.signUpCheck("A", signUpCheckRequest)
    }

    override suspend fun finishReg(
        finishRegRequest: FinishRegRequest
    ): Resource<SignInResponse> = getResult {
        userApiService.finishReg(finishRegRequest)
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Resource<BaseResponse> = getResult {
        userApiService.changePassword(changePasswordRequest)
    }

    override suspend fun changePasswordWithoutSMS(
        token: String,
        changePasswordRequest: ChangePasswordRequest
    ): Resource<BaseResponse> = getResult {
        userApiService.changePassWithoutSMS(token, changePasswordRequest)
    }


    override suspend fun logOut(
        token: String, logOutRequest: LogOutRequest
    ): Resource<BaseResponse> = getResult {
        userApiService.logOut(token, logOutRequest)
    }

    override suspend fun checkIdentification(
        token: String, userIdentification: CheckIdentification
    ): Resource<BaseResponse> = getResult {
        userApiService.checkIdentification(token, userIdentification)
    }

    override suspend fun getAccessToken(
        clientToken: String,
        myIdGetAccessTokenRequest: MyIdGetAccessTokenRequest
    ): Resource<MyIdMeResponse> = getResult {
        userApiService.getAccessTokenMyId(clientToken, myIdGetAccessTokenRequest)
    }

    override suspend fun editUserInfo(
        token: String, editUserInfo: EditUserInfo
    ): Resource<BaseResponse> = getResult {
        userApiService.editUserInfo(token, editUserInfo)
    }

    override suspend fun sendEmailCode(sendEmailCode: SendEmailCode): Resource<SignInResponse> = getResult {
        userApiService.sendEmailCode(sendEmailCode)
    }

    override suspend fun changeNotificationState(
        token: String,
        request: ChangeNotifStateRequest
    ): Resource<BaseResponse> = getResult {
        userApiService.changeNotificationState(token, request)
    }

    override suspend fun getUserResidency(token: String): Resource<ResidencyResponse> = getResult {
        userApiService.getUserResidency(token)
    }

}