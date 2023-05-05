package uz.fido.network.domain.model.payment

import java.io.Serializable

class Cheque(
    var key: String,
    var key_description: String,
    var value: String,
    var order: Int
): Serializable