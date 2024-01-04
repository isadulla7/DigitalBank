package uz.fido.network.domain.model.my_id

data class CheckIdentification(
    val doc_serial: String,
    val doc_number: String,
    val birthday: String,
    val doc_type: String? = "06"
)