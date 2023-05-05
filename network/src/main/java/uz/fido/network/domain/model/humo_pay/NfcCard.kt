package uz.fido.network.domain.model.humo_pay

import java.io.Serializable

data class NfcCard(
    val externalId: String,
    var isDefault: Boolean = true,
//    var cardStatus: CardStatus? = null,
    var balance: String = "0",
    var cardName: String = ""
): Serializable