package uz.fido.network.domain.model.applications

data class ApplicationStatus(
    val state_name: String,
    val create_date: String? = null,
    val state_id: Int = 0,
    val err_msg: String? = null,
    val status: String? = null,
    var isEnable: Boolean = false,
    val cardNumber: String? = null,
    val cardExpiry: String? = null
)