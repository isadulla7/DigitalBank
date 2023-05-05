package uz.fido.network.domain.model.inn

data class GetDebtByInnRequest(
    val inn: String,
    val command: String = "info"
)