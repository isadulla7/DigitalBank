package uz.fido.network.domain.model.deposits.constructor

data class DepositConstPercent(
    val min: String,
    val max: String? = null,
    val inCapitalInAdd: String,
    val inCapitalOutAdd: String,
    val outCapitalOutAdd: String,
    val outCapitalInAdd: String,
    val currency_code: String,
    val code: Int,
    val msg: String,
    val request_id: Int
)