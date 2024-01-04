package uz.fido.network.domain.model.deposits.constructor

data class DepositConstPercentRequest(
    val depId: String,
    val dcParam100: String,
    val dcParam105: String,
    val dcParam103: String
)