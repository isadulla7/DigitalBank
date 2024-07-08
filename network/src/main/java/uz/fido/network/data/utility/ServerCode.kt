package uz.fido.network.data.utility

enum class ServerCode(val code: Int) {
    SERVICE_UNAVAILABLE(503),
    TECHNICAL_WORKS(502),
    SERVER_ERROR(500),
    TOKEN_EXPIRED(406),
    NOT_FOUND(404),
    UNAUTHORIZED(401),
    BAD_REQUEST(400),
    SUCCESS(200),
    SOCKET_TIME_OUT(-1),
}