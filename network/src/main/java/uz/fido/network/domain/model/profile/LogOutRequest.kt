package uz.fido.network.domain.model.profile

data class LogOutRequest(
    val device_code: String,
    val device_type: String,
    val fcm_token: String,
    val phone_number: String,
    val sim_iccd: String? = null,
    val network_state: String? = null,
    val imei_data: String? = null,
    val os_system_version_api: String? = null,
    val client_id: String? = null
)