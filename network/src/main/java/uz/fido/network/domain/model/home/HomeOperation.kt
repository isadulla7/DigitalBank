package uz.fido.network.domain.model.home

import java.io.Serializable

class HomeOperation(
    var id: Int = 0,
    var name: String = "",
    var icon: String = "",
    var position: Int = 100,
    var chosen: Boolean = false,
    var type: Int,
    var isVisible: Boolean = true,
    var order: Int? = null
) : Serializable