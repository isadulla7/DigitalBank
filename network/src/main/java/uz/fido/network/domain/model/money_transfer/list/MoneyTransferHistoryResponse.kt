package uz.fido.network.domain.model.money_transfer.list

data class MoneyTransferHistoryResponse(
    val code: Int,
    val msg: String,
    val data: ArrayList<MoneyTransferHistory>
)