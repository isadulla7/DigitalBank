package uz.fido.network.domain.model.search

import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentService
import java.io.Serializable

class SearchItem(
    val paymentService: PaymentService?,
    val paymentGroup: PaymentGroup?,
    val name: String?,
    val imageName: String?,
    val groupName: String?,
    val id: String?
) : Serializable
