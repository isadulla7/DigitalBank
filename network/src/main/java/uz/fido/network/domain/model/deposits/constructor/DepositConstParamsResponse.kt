package uz.fido.network.domain.model.deposits.constructor

data class DepositConstParamsResponse(
    val code: Int,
    val dcParam100: List<String>,
    val dcParam101: List<String>,
    val dcParam102: List<String>,
    val dcParam103: List<String>,
    val dcParam104: List<String>,
    val dcParam105: ArrayList<DepositConstPercent>,
    val dcParam200: List<String>,
    val dcParam201: List<String>,
    val dcParam202: List<String>,
    val dcParam203: List<String>,
    val dcParam204: List<String>,
    val dcParam205: List<String>,
    val dcParam300: String,
    val dcParam301: String,
    val dcParam302: String,
    val dcParam303: String,
    val dcParam304: String,
    val dcParam305: String,
    val depId: Int,
    val msg: String,
    val request_id: Int
)