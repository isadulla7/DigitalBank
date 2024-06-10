package uz.fido.network.domain.model.edit_user

import java.io.Serializable

data class EditUserInfo(
    val email: String? = null,
    val name: String? = null,
    val nick_name: String? = null,
    val password: String? = null,
    val patronymic: String? = null,
    val surname: String? = null,
    val date_of_birth: String? = null,
    val gender: String? = null,
    val user_avatar: String? = null
):Serializable