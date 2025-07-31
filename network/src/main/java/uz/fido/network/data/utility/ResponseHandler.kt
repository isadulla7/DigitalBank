package uz.fido.network.data.utility

import retrofit2.HttpException
import uz.fido.network.domain.model.abc_base.CustomException
import java.net.SocketTimeoutException

suspend fun <T : Any> getResult(data: suspend () -> T): Resource<T> {
    return try {
        handleSuccess(data())
    } catch (e: Exception) {
        handleException(e)
    }
}

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1)
}

fun <T : Any> handleSuccess(data: T): Resource<T> {
    return Resource.success(data)
}

fun <T : Any> handleException(e: Exception): Resource<T> {
    val errorResource: Resource<T>
    when (e) {
        is HttpException -> {
            val error = ErrorUtils.parseError(e.response()!!)
            errorResource = when (error.code) {
                700 -> {
                    Resource.error(
                        message = "NEED_IDENTIFIED", data = null, errorBody = error
                    )
                }

                1525, -777 -> {
                    Resource.error(
                        message = "LOG_OUT", data = null, errorBody = error
                    )
                }

                else -> {
                    Resource.error(
                        message = error.message, data = null, errorBody = error
                    )
                }
            }
        }

        is SocketTimeoutException -> {
            errorResource = Resource.error(
                message = getErrorMessage(
                    ErrorCodes.SocketTimeOut.code, e.message
                ), data = null
            )
        }

        else -> {
            errorResource = Resource.error(message = getErrorMessage(Int.MAX_VALUE, e.message), data = null)
        }
    }
    return errorResource
}

fun <T : Any> handleException(e: CustomException): Resource<T> {
    val error = ErrorUtils.vpnError(e)
    return Resource.error(
        message = error.message, data = null, errorBody = error
    )
}

private fun getErrorMessage(code: Int, message: String?): String {
    return when (code) {
        ServerCode.SOCKET_TIME_OUT.code -> "Проблема при подключении к системе. Пожалуйста, обратитесь к администратору."
        ServerCode.UNAUTHORIZED.code -> "Неавторизованный"
        ServerCode.NOT_FOUND.code -> "Не найден"
        ServerCode.TOKEN_EXPIRED.code -> "Срок действия токена истек"
        ServerCode.SERVER_ERROR.code -> "Ошибка в сервере"
        ServerCode.TECHNICAL_WORKS.code -> "Технические неполадки"
        ServerCode.SERVICE_UNAVAILABLE.code -> "Технические неполадки"
        else -> "Code: $code;\nMessage: $message"
    }
}



