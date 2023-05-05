package uz.fido.network.domain.model.inn

data class InnDebtInfo(
    val currentdate: String,
    val customer_Type: String,
    val nedoimka: String,
    val ns10code: String,
    val ns10name: String,
    val ns11code: String,
    val ns11name: String,
    val objectcode: String,
    val objectname: String,
    val penya: String,
    val pereplata: String,
    val reason: String,
    val success: String,
    val tin: String
)