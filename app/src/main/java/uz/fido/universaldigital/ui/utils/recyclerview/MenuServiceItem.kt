package uz.fido.universaldigital.ui.utils.recyclerview

import java.io.Serializable

class MenuServiceItem(
    val icon: Int? = 0,
    val serviceName: String,
    val serviceId: Int,
    val serviceDescription: String? = null
) : Serializable
