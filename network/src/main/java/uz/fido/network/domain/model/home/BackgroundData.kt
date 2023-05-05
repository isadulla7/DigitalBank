package uz.fido.network.domain.model.home

import java.io.Serializable

class BackgroundData(
    var image: String,
    var id: Int = 0,
    var selected: Boolean = false
) : Serializable