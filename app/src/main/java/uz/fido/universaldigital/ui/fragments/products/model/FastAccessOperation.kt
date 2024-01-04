package uz.fido.universaldigital.ui.fragments.products.model

import java.io.Serializable

class FastAccessOperation(
    var id: Int = 0,
    var name: String,
    var icon: String,
    var isVisible: Boolean = false,
    var order: Int? = null,
    var viewType: Int? = 1,
    var description: String? = "",
    var isEnabled: Boolean = true
) : Serializable