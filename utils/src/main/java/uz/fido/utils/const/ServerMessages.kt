package uz.fido.utils.const

import androidx.fragment.app.Fragment
import uz.fido.utils.R

object ServerMessages {

    const val ERROR_CODE_VPN = "444"
    const val NEED_IDENTIFIED = "NEED_IDENTIFIED"
    private const val ERROR_MESSAGE_BAD = "bad"
    private const val ERROR_MESSAGE_ORA = "ORA"
    private const val ERROR_MESSAGE_CERTIFICATE = "CertPathValidatorException"
    private const val ERROR_MESSAGE_RESOLVE_HOST = "Unable to resolve host"
    private const val ERROR_MESSAGE_FAILED_TO_CONNECT = "Failed to connect"
    private const val ERROR_MESSAGE_TOKEN_EXPIRED = "Token expired"
    private const val ERROR_MESSAGE_SESSION_EXPIRED = "Session timeout is expired"

    fun Fragment.getMeaningFulMessage(message: String): String {
        var meaningFulMessage = message
        when {
            message == ERROR_MESSAGE_BAD -> {
                meaningFulMessage = getString(R.string.please_try_again_later)
            }

            message.contains(ERROR_MESSAGE_ORA) -> {
                meaningFulMessage = getString(R.string.please_try_again_later)
            }

            message.contains(ERROR_MESSAGE_CERTIFICATE) -> {
                meaningFulMessage = getString(R.string.error_security_connection)
            }

            message.contains(ERROR_MESSAGE_RESOLVE_HOST) -> {
                meaningFulMessage = getString(R.string.slow_internet_connection)
            }

            message.contains(ERROR_MESSAGE_FAILED_TO_CONNECT) -> {
                meaningFulMessage = getString(R.string.tech_works)
            }

            message.isEmpty() -> {
                meaningFulMessage = getString(R.string.unkknown_error)
            }

            message == ERROR_MESSAGE_TOKEN_EXPIRED || message == ERROR_MESSAGE_SESSION_EXPIRED -> {
                meaningFulMessage = ""
            }
        }
        return meaningFulMessage
    }

}