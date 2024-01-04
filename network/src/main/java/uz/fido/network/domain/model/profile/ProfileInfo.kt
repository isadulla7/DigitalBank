package uz.fido.network.domain.model.profile

import java.io.Serializable

data class ProfileInfo(
    val token: String,
    val user_id: String,
    val name: String? = null,
    val surname: String? = null,
    val patronymic: String? = null,
    val user_type_color: String? = null,
    val user_type_name: String? = null,
    val user_type_id: Int? = null,
    val filial_code: String? = null,
    val email: String? = null,
    val date_of_birth: String? = null,
    val gender: String? = null
) : Serializable