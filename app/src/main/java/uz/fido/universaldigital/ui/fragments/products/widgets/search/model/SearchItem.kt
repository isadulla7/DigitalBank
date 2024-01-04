package uz.fido.universaldigital.ui.fragments.products.widgets.search.model

import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentService
import java.io.Serializable

class SearchItem(
    val paymentService: PaymentService? = null,
    val paymentGroup: PaymentGroup? = null,
    val name: String? = "",
    val imageName: String? = "",
    val groupName: String? = "",
    val id: String? = ""
) : Serializable
