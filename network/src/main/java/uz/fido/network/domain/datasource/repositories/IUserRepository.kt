package uz.fido.network.domain.datasource.repositories

import uz.fido.network.data.utility.Resource
import uz.fido.network.domain.model.abc_base.BaseResponse
import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.network.domain.model.my_id.CheckIdentification
import uz.fido.network.domain.model.my_id.GetPhysicalPhoto
import uz.fido.network.domain.model.my_id.GetPhysicalPhotoResponse
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

interface IUserRepository {

    suspend fun getPaymentFile(
        token: String
    ): Resource<Payment>

    suspend fun checkUserSignIn(
        requestBody: CheckUserSignInRequest
    ): Resource<BaseResponse>

    suspend fun checkUserSms(
        checkUserSms: CheckUserSms
    ): Resource<BaseResponse>

    suspend fun signIn(
        signInRequest: SignInRequest
    ): Resource<SignInResponse>

    suspend fun signUpCheck(
        signUpCheckRequest: SignUpCheckRequest
    ): Resource<BaseResponse>

    suspend fun signUp(
        signUpRequest: SignUpRequest
    ): Resource<SignInResponse>

    suspend fun checkForgetPassword(checkForgetPasswordModel: CheckForgetPasswordModel): Resource<BaseResponse>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Resource<BaseResponse>

    suspend fun logOut(
        token: String,
        logOutRequest: LogOutRequest
    ): Resource<BaseResponse>

    suspend fun getPhysicalPhoto(
        getPhysicalPhoto: GetPhysicalPhoto
    ): Resource<GetPhysicalPhotoResponse>

    suspend fun checkIdentification(
        token: String,
        userIdentification: CheckIdentification
    ): Resource<BaseResponse>

    suspend fun editUserInfo(
        token: String,
        editUserInfo: EditUserInfo
    ): Resource<BaseResponse>

    suspend fun getActiveSessions(
        token: String,
        getUserDevicesRequest: GetUserDevicesRequest
    ): Resource<GetUserDevicesResponse>

    suspend fun checkDeviceRequest(
        token: String,
        checkDeviceRequest: CheckDeviceRequest
    ): Resource<BaseResponse>

    suspend fun deleteSession(
        token: String,
        deleteUserDeviceRequest: DeleteUserDeviceRequest
    ): Resource<BaseResponse>
}