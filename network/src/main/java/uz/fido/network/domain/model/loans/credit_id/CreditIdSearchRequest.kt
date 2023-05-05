package uz.fido.network.domain.model.loans.credit_id

data class CreditIdSearchRequest(
    val docType: String = "6",
    val series: String,
    val number: String
)