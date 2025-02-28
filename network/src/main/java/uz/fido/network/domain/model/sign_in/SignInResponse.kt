package uz.fido.network.domain.model.sign_in

import uz.fido.network.domain.model.abc_base.BaseResponse
import java.io.Serializable

data class SignInResponse(
    val ora_msg: String,
    val token: String,
    val user_id: String,
    var name: String,
    var surname: String,
    val patronymic: String,
    val user_type_color: String,
    val user_type_name: String,
    val user_type_id: Int,
    val version: String? = "0",
    val filial_code: String? = null,
    var email: String? = null,
    var date_of_birth: String? = null,
    var gender: String? = null,
    val points: String? = null,
    val user_status_name: String,
    var user_status_id: String,
    val user_avatar: String,
    var password: String? = null,
    var phone_number: String? = null,
    val passport_number: String,
    val passport_registration_date: String,
    val user_exist: String,
    val passport_serial: String,
    val birthday: String,
    val is_authenticate: String,
    val is_card_exist: String,
    val is_email: String,
    val password_enc: String,
    val application_count: Int? = 0,
    val device_myid_state: String? = null
) : Serializable, BaseResponse()