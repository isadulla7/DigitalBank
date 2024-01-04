package uz.fido.network.data.utility

import uz.fido.network.domain.model.abc_base.APIError


data class Resource<out T>(val status: Status, val data: T?, val message: String?, val errorBody: APIError?) {

    companion object {

        fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null, errorBody = null)

        fun <T> error(data: T?, message: String, errorBody: APIError? = null): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message, errorBody = errorBody)

    }
}