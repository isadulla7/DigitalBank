package uz.fido.network.domain.model.payment.qr

import java.io.Serializable

data class QrCode (
    var name: String? = null,
    var id: String? = null,
    var value: String? = null,
    var length: Int? = null
): Serializable