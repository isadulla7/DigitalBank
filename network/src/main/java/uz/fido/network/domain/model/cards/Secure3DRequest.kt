package uz.fido.network.domain.model.cards

import java.io.Serializable

data class Secure3DRequest(
    val from_object_id: String,
    val operation_type: String
) : Serializable

enum class Secure3DOperations(val operation_type: String) {
    INSERT("1"),
    UPDATE_PHONE_CHANGE("2"),
    UPDATE_AUTH_METHOD_FROM_OTP_TO_PWD("3"),
    UPDATE_PWD_CHANGE("3"),
    UPDATE_AUTH_METHOD_FROM_PWD_TO_OTP("4"),
    UPDATE_CLIENT_STATUS("5"),
    STATUS_CHECK("6"),
    COPY("7"),
    DELETE("8"),
}

data class secure3DResponse(
    val status: String,
    val code: Int,
    val msg: String
)