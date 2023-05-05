package uz.fido.network.domain.model.mib

import java.io.Serializable

data class Mib(
    var client_type: String? = null,
    var doc_value: String? = null,
    var created_on: String? = null
) : Serializable