package uz.fido.network.domain.model.devices

data class Device(
    val ip: String,
    val name: String,
    val code: String,
    val app_version: String,
    val date: String
)