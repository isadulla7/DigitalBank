package uz.fido.network.domain.model.money_transfer

import java.io.Serializable

class MoneySendRequest: Serializable {
    var card_number: String? = null
    var client_phone: String? = null
    var remittance_code: String? = null
    var remittance_amount: String? = null
    var remittance_type_id: String? = null
    var last_name: String? = null
    var first_name: String? = null
    var patronymic: String? = null
    var country_code: String? = null
    var operation_id: Int? = 1
    var cor_doc_type: String? = null
    var cor_doc_serial: String? = null
    var cor_doc_number: String? = null
    var cor_address: String? = null
    var cor_birth_date: String? = null
    var cor_birth_place: String? = null
    var cor_citizenship: String? = null
    var city: String? = null
    var cor_phone: String? = null
}