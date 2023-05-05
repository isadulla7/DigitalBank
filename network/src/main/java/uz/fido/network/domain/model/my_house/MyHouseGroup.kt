package uz.fido.network.domain.model.my_house

import java.io.Serializable

data class MyHouseGroup(
    val id: String? = null,
    var name: String? = null,
    var order: Int? = null
) : Serializable