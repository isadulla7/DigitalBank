package uz.fido.network.domain.model.account

import java.io.Serializable

class Account(
    val name: String,
    val img: String,
    var isChecked: Boolean = false
) : Serializable
