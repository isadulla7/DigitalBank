package uz.fido.network.domain.model.sessions

import java.io.Serializable

data class UserDevices(
    val phone_number: String,
    val fcm_token: String,
    var device_code: String,
    val country: String,
    val status: String,
    val device_type: String,
    val last_seen_date: String,
    val city: String,
    val ip: String,
    val created_on: String,
    val device_name: String,
    val online:String="false",
    var my_device_code:String="",
) : Serializable