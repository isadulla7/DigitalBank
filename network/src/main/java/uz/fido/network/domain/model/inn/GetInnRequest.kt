package uz.fido.network.domain.model.inn

import java.io.Serializable

data class GetInnRequest(
    val command: String,
    val pass_serial: String,
    val pass_number: String,
    val birth_date: String,
    val doc_type: String = "1"
) : Serializable