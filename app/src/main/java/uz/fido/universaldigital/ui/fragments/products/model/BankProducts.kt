package uz.fido.universaldigital.ui.fragments.products.model

import java.io.Serializable

class BankProducts(
    var id: Int = 0,
    var name: String,
    var icon: String,
    var description: String? = ""
) : Serializable