package uz.fido.network.data.utility

import org.json.JSONObject
import retrofit2.Response
import uz.fido.network.domain.model.abc_base.APIError
import uz.fido.network.domain.model.abc_base.CustomException

object ErrorUtils {

    fun parseError(response: Response<*>): APIError {
        if (response.errorBody() != null) {
            return try {
                val jsonObject = JSONObject(response.errorBody()!!.string())
                val code = jsonObject.getInt("code")
                var message: String? = jsonObject.getString("msg")
                if (message == null) {
                    message = "Неизвестная ошибка_"
                }
                if ((message.contains("502 Bad"))) {
                    message = "$message ошибка подключения к серверу"
                }
                APIError(
                    code,
                    message,
                    response.errorBody()!!.string()
                )
            } catch (e: Exception) {
                when (response.code()) {
                    ServerCode.TECHNICAL_WORKS.code -> {
                        APIError(
                            ServerCode.TECHNICAL_WORKS.code,
                            "Ошибка подключения к серверу"
                        )
                    }

                    ServerCode.TOKEN_EXPIRED.code -> {
                        APIError(
                            ServerCode.TOKEN_EXPIRED.code,
                            "Срок действия токена истек"
                        )
                    }

                    ServerCode.SERVER_ERROR.code -> {
                        APIError(
                            ServerCode.SERVER_ERROR.code,
                            "Внутренняя ошибка сервера"
                        )
                    }

                    ServerCode.SERVICE_UNAVAILABLE.code -> {
                        APIError(
                            ServerCode.SERVICE_UNAVAILABLE.code,
                            "Сервер временно недоступен"
                        )
                    }

                    else -> {
                        APIError(
                            ServerCode.BAD_REQUEST.code,
                            "Неизвестная ошибка"
                        )
                    }
                }
            }
        }
        return if (response.code() == ServerCode.TOKEN_EXPIRED.code) {
            APIError(
                ServerCode.TOKEN_EXPIRED.code,
                "Срок действия токена истек"
            )
        } else {
            APIError(ServerCode.BAD_REQUEST.code, "Неизвестная ошибка")
        }
    }

    fun vpnError(exception: CustomException): APIError {
        return APIError(
            444,
            exception.message
        )
    }
}