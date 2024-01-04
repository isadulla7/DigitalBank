package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class DocData(
    val expiry_date: String,
    val issued_by: String,
    val issued_date: String,
    val pass_data: String,
    val doc_type: String,
    val doc_type_id: String? = null
) : Serializable