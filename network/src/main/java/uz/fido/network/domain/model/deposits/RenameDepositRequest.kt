package uz.fido.network.domain.model.deposits

data class RenameDepositRequest(
    val dep_name: String,
    val savDepId: String
)