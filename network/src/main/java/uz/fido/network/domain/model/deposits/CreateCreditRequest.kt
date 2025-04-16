package uz.fido.network.domain.model.deposits

data class CreateCreditRequest(
    val amount: String,
    val command: String,
    val depId: String,
    val depType: String,
    val from_object_id: String? = null,
    val service_id: String,
    val pay_to_card: String = "N",
    val pay_to_card_number: String? = null,
    val dep_construct: String? = null,
    val dcParam100: String? = null,
    val dcParam102: String? = null,
    val dcParam103: String? = null,
    val dcParam104: String? = null,
    val dcParam105: String? = null,
    val dcParam200: String? = null,
    val dcParam201: String? = null,
    val dcParam202: String? = null,
    val dcParam203: String? = null,
    val dcParam204: String? = null,
    val dcParam205: String? = null,
    val dep_name: String? = null,
    val sms_code: String? = null,
    val string_line: String? = null,
    val bxm_code: String? = null
)